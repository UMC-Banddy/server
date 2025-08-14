package com.umc.banddy.domain.chat.service;


import com.umc.banddy.domain.chat.converter.ChatConveter;
import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.chat.domain.enums.Role;
import com.umc.banddy.domain.chat.domain.enums.RoomType;
import com.umc.banddy.domain.chat.domain.enums.Type;
import com.umc.banddy.domain.chat.repository.ChatMessageRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomParticipantRepository;
import com.umc.banddy.domain.chat.repository.ChatCustomRepository;
import com.umc.banddy.domain.chat.web.dto.MessageAuthenticationHeader;
import com.umc.banddy.domain.chat.web.dto.TimeMark;
import com.umc.banddy.domain.chat.web.dto.message.*;
import com.umc.banddy.domain.chat.web.dto.MessageType;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Status;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.umc.banddy.domain.chat.converter.ChatConveter.toChatMessageResponse;
import static com.umc.banddy.domain.chat.converter.ChatConveter.toWsMessage;


@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final WebsocketService websocketService;
    private final ChatCustomRepository chatCustomRepository;
    private final ChatRoomParticipantCache participantCache;
    private final ChatService chatService;
    private final MemberRepository memberRepository;
    private final ChatRoomParticipantCache chatRoomParticipantCache;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    // 채팅 메세지 저장
    @Transactional
    public ChatMessage saveMessage(ChatRoom chatRoom, Member member, String content, Type type) {
        ChatMessage chatMessage = ChatMessage.builder()
                .member(member)
                .chatRoom(chatRoom)
                .type(type)
                .content(content)
                .build();
        return chatMessageRepository.save(chatMessage);
    }

    // 채팅 응답 반환
    public ChatMessageResponse chatToResponse(ChatMessage chatMessage) {return toChatMessageResponse(chatMessage);}

    @Transactional
    @CacheEvict(value = "roomParticipants", key = "#chatRoom.id")
    public ChatSystemResponse exitChatRoom(ChatRoom chatRoom, Member member){
        ChatRoomParticipant chatRoomParticipant
                = participantRepository
                .findByChatRoomAndMemberAndStatus(chatRoom, member, Status.ACTIVE)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHAT_NOT_PARTICIPATED));
        
        chatRoomParticipant.setStatus(Status.INACTIVE);
        participantRepository.save(chatRoomParticipant);

        ChatMessage chatMessage = saveMessage(chatRoom, member, member.getNickname() + "님이 채팅방을 나갔습니다.", Type.SYSTEM);

        websocketService.topicMessage(
                chatRoom.getId(),
                toWsMessage(
                        ChatMessageResponse.builder()
                                .messageId(chatMessage.getId())
                                .roomId(chatRoom.getId())
                                .content(chatMessage.getContent())
                                .senderId(member.getId())
                                .type(Type.SYSTEM)
                                .senderName("System")
                                .timestamp(chatMessage.getCreatedAt())
                                .build()
                , MessageType.SYSTEM
                )

        );

        return ChatSystemResponse.builder()
                .roomId(chatRoom.getId())
                .message(chatMessage.getContent())
                .timestamp(chatMessage.getCreatedAt())
                .build();
    }

    @Transactional
    @CacheEvict(value = "roomParticipants", key = "#chatRoom.id")
    public ChatSystemResponse joinChatRoom(ChatRoom chatRoom, Member member){

        // 첫참여, 이미 참여중, 참여기록 있음 분기
        ChatRoomParticipant participant
                = participantRepository.findByChatRoomAndMember(chatRoom, member)
                .map(p ->{
                    if (p.getStatus() == Status.INACTIVE) {
                        p.setStatus(Status.ACTIVE);
                    } else if (p.getStatus() == Status.ACTIVE) {throw new GeneralException(ErrorStatus.CHAT_ALREADY_PARTICIPATED);}
                    return p;
                }).orElseGet(() -> ChatRoomParticipant.builder()
                        .chatRoom(chatRoom)
                        .member(member)
                        .role(Role.MEMBER)
                        .status(Status.ACTIVE)
                        .lastReadMessageId(0L)
                        .build());

        if(chatRoom.getRoomType() == RoomType.BAND) {
            if (chatRoom.getBandChat() == null) {
                throw new GeneralException(ErrorStatus.CHAT_TYPE_NOT_MATCHED);
            }else if (chatRoomParticipantCache.getParticipants(chatRoom.getId()).size() >= 2){
                throw new GeneralException(ErrorStatus.CHAT_TYPE_NOT_MATCHED);
            }
            throw new GeneralException(ErrorStatus.CHAT_CAN_NOT_BE_JOINED);
        }else if(chatRoom.getRoomType() == RoomType.PRIVATE) {
            if (chatRoom.getBandChat() != null) {
                throw new GeneralException(ErrorStatus.CHAT_TYPE_NOT_MATCHED);
            } else if (chatRoomParticipantCache.getParticipants(chatRoom.getId()).size() >= 2){
                throw new GeneralException(ErrorStatus.CHAT_CAN_NOT_BE_JOINED);
            }
        }

        participantRepository.save(participant);

        ChatMessage chatMessage =  saveMessage(chatRoom, member, member.getNickname() + "님이 채팅방에 참여하셨습니다.", Type.SYSTEM);

    if (chatRoom.getRoomType() == RoomType.GROUP) {
        websocketService.topicMessage(
                chatRoom.getId(),
                toWsMessage(
                        ChatMessageResponse.builder()
                                .messageId(chatMessage.getId())
                                .roomId(chatRoom.getId())
                                .content(chatMessage.getContent())
                                .senderId(member.getId())
                                .type(Type.SYSTEM)
                                .senderName("System")
                                .timestamp(chatMessage.getCreatedAt())
                                .build()
                        , MessageType.SYSTEM
                )
        );
    } else if (chatRoom.getRoomType() == RoomType.PRIVATE || chatRoom.getRoomType() == RoomType.BAND) {
        Set<String> allParticipants = participantCache.getParticipants(chatRoom.getId());
        for (String email : allParticipants) {
            websocketService.queuePrivateMessage(
                    email,
                    chatRoom.getId(),
                    toWsMessage(
                            ChatMessageResponse.builder()
                                    .messageId(chatMessage.getId())
                                    .roomId(chatRoom.getId())
                                    .content(chatMessage.getContent())
                                    .senderId(member.getId())
                                    .type(Type.SYSTEM)
                                    .senderName("System")
                                    .timestamp(chatMessage.getCreatedAt())
                                    .build()
                            , MessageType.SYSTEM
                    )
            );
        }
    }

        return ChatSystemResponse.builder()
                .roomId(chatRoom.getId())
                .message(chatMessage.getContent())
                .timestamp(chatMessage.getCreatedAt())
                .build();
    }

    public CursorChatMessageResponse getChatMessages(Long roomId, Long cursor, Integer limit) {

        List<CursorChatMessage> ccm
                = chatCustomRepository.findByRoomIdWithCursorAsDto(roomId, cursor, limit);

        return CursorChatMessageResponse.builder()
                .messages(ccm)
                .hasNext(ccm.size() == limit)
                .lastMessageId(ccm.isEmpty() ? null : ccm.get(ccm.size() - 1).getMessageId())
                .build();
    }

    public void sendChatMessage(Long roomId, Long userId, ChatMessageRequest messageRequest) {

        Pair<ChatRoom, Member> pair = chatService.verifedChatRoomAndMember(roomId, userId);

        ChatRoom chatRoom = pair.getLeft();
        Member member = pair.getRight();

        // 채팅 메세지 저장
        ChatMessage chatMessage = saveMessage(chatRoom,member, messageRequest.getContent(), Type.TEXT);

        Set<String> allParticipants =  participantCache.getParticipants(roomId); // 이메일 기준
        Set<String> subscribedUsers = chatService.getSubscribedUserEmails(roomId);

        // 성능상 개선 여지 있음
        Set<String> unsubscribedUsers = new HashSet<>(allParticipants);
        unsubscribedUsers.removeAll(subscribedUsers);

        RoomType roomType = chatRoom.getRoomType();

        // 전송 로직 분기
        if (roomType.equals(RoomType.GROUP)) {
            // GROUP: 토픽 브로드캐스트
            ChatMessageResponse chatMessageResponse = chatToResponse(chatMessage);
            websocketService.topicMessage(roomId, toWsMessage(chatMessageResponse, MessageType.MESSAGE));
            // 비구독자 처리
            UnreadPrivateResponseImpl unreadResponse = UnreadPrivateResponseImpl.builder()
                    .senderId(member.getId())
                    .roomId(chatRoom.getId())
                    .content(chatMessage.getContent())
                    .timestamp(chatMessage.getCreatedAt())
                    .build();
            for (String email : allParticipants) {
                if (unsubscribedUsers.contains(email)) {
                    websocketService.queueUnreadMessage(email, toWsMessage(unreadResponse, MessageType.UNREAD_MESSAGE));
                }
            }
        } else if (roomType.equals(RoomType.PRIVATE) ) {
            // PRIVATE: 세션 단위로 유저에게 개별 전송
            Long receiverId = Optional.ofNullable(messageRequest.getReceiverId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.PRIVATE_CHAT_NEED_RECEIVER));
            // 캐싱 고려
            String receiverEmail = memberRepository.findEmailById(receiverId);
            if (unsubscribedUsers.contains(receiverEmail)) {
                UnreadPrivateResponseImpl unreadResponse = UnreadPrivateResponseImpl.builder()
                        .senderId(member.getId())
                        .roomId(chatRoom.getId())
                        .content(chatMessage.getContent())
                        .timestamp(chatMessage.getCreatedAt())
                        .build();
                websocketService.queueUnreadMessage(receiverEmail, toWsMessage(unreadResponse, MessageType.UNREAD_MESSAGE));

            } else {
                ChatMessageResponse chatMessageResponse = chatToResponse(chatMessage);
                websocketService.queuePrivateMessage(receiverEmail, roomId, toWsMessage(chatMessageResponse, MessageType.MESSAGE));
            }
        } else if (roomType.equals(RoomType.BAND)) {
            // BAND: 세션 단위로 유저에게 개별 전송
            Long receiverId = Optional.ofNullable(messageRequest.getReceiverId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.PRIVATE_CHAT_NEED_RECEIVER));
            // 캐싱 고려
            String receiverEmail = memberRepository.findEmailById(receiverId);
            if (unsubscribedUsers.contains(receiverEmail)) {
                UnreadResponseImpl unreadbandResponse = UnreadResponseImpl.builder()
                        .senderId(member.getId())
                        .roomId(chatRoom.getId())
                        .bandId(chatRoom.getBandChat().getBand().getId())
                        .content(chatMessage.getContent())
                        .timestamp(chatMessage.getCreatedAt())
                        .build();
                websocketService.queueUnreadMessage(receiverEmail, toWsMessage(unreadbandResponse, MessageType.UNREAD_MESSAGE));
            } else {
                ChatMessageResponse chatMessageResponse = chatToResponse(chatMessage);
                websocketService.queuePrivateMessage(receiverEmail, roomId, toWsMessage(chatMessageResponse, MessageType.MESSAGE));
            }

        }
    }

    public void sendPrivateMessage(
            Long roomId,
            MessageAuthenticationHeader auth,
            PrivateChatMessageRequest messageRequest
    ){
        Pair<ChatRoom, Member> pair = chatService.verifedChatRoomAndMember(roomId, auth.getMemberId());

        ChatRoom chatRoom = pair.getLeft();
        Member member = pair.getRight();

        RoomType reqType = messageRequest.getRoomType();
        if (reqType != RoomType.PRIVATE && reqType != RoomType.BAND) {
            throw new GeneralException(ErrorStatus.CHAT_TYPE_NOT_MATCHED);
        }
        if (chatRoom.getRoomType() != reqType) {
            throw new GeneralException(ErrorStatus.CHAT_TYPE_NOT_MATCHED);
        }

        // 채팅 메세지 저장
        ChatMessage chatMessage = saveMessage(chatRoom, member, messageRequest.getContent(), Type.TEXT);

        // 채팅방 참여자 정보
        Set<String> allParticipants = Optional.ofNullable(participantCache.getParticipants(roomId))
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHAT_NOT_PARTICIPATED));
        if (allParticipants.size() != 2)
            throw new GeneralException(ErrorStatus.CHATROOM_INVALID_PARTICIPANTS);

        // 지금 수신 중인 사용자 정보 불러오기
        Set<String> subscribedUsers = chatService.getPrivateSubscribedUserEmails(roomId);

        // 내가 아닌 사용자의 인증정보(email)
        String receiverEmail = allParticipants.stream()
                .filter(m -> !Objects.equals(m, auth.getName()))
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHAT_NOT_PARTICIPATED));

        if(subscribedUsers.contains(receiverEmail)){
            ChatMessageResponse chatMessageResponse = chatToResponse(chatMessage);
            websocketService.queuePrivateMessage(
                    receiverEmail,
                    roomId,
                    toWsMessage(chatMessageResponse, MessageType.MESSAGE)
            );
        }else{
            UnreadResponse unreadResponse = ChatConveter.toUnreadResponse(member, chatRoom, chatMessage, chatRoom.getRoomType());
            websocketService.queueUnreadMessage(
                    receiverEmail, toWsMessage(unreadResponse, MessageType.UNREAD_MESSAGE));
        }
    }
    public void sendGroupMessage(
            Long roomId,
            MessageAuthenticationHeader auth,
            GroupChatMessageRequest  messageRequest
    ){
        Pair<ChatRoom, Member> pair = chatService.verifedChatRoomAndMember(roomId, auth.getMemberId());

        ChatRoom chatRoom = pair.getLeft();
        Member member = pair.getRight();

        RoomType reqType = messageRequest.getRoomType();
        if (reqType != RoomType.GROUP) {
            throw new GeneralException(ErrorStatus.CHAT_TYPE_NOT_MATCHED);
        }
        if (chatRoom.getRoomType() != reqType) {
            throw new GeneralException(ErrorStatus.CHAT_TYPE_NOT_MATCHED);
        }
        // 채팅 메세지 저장
        ChatMessage chatMessage = saveMessage(chatRoom, member, messageRequest.getContent(), Type.TEXT);

        // 채팅방 참여자 정보
        Set<String> allParticipants =  participantCache.getParticipants(roomId);

        // 지금 수신 중인 사용자 정보 불러오기
        Set<String> subscribedUsers = chatService.getGroupSubscribedUserEmails(roomId);

        //온라인(구독 중) 사용자
        Set<String> onlineUsers = allParticipants.stream()
                .filter(subscribedUsers::contains)
                .collect(Collectors.toSet());

        // 오프라인 사용자
        Set<String> offlineUsers = new HashSet<>(allParticipants);
        offlineUsers.removeAll(onlineUsers);

        ChatMessageResponse chatMessageResponse = chatToResponse(chatMessage);
        websocketService.topicMessage(
                roomId,
                toWsMessage(chatMessageResponse, MessageType.MESSAGE)
        );
        UnreadResponse unreadResponse = ChatConveter.toUnreadResponse(member, chatRoom, chatMessage, chatRoom.getRoomType());

        offlineUsers.forEach(receiverEmail -> {
                    websocketService.queueUnreadMessage(
                            receiverEmail,
                            toWsMessage(unreadResponse, MessageType.UNREAD_MESSAGE));
                });
    }


    public void sendPrivateLastRead(Long roomId, MessageAuthenticationHeader auth, Long messageId) {

        ChatRoomParticipant participant =
                chatRoomParticipantRepository.findByChatRoom_IdAndMember_IdAndStatus(roomId, auth.getMemberId(), Status.ACTIVE )
                        .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPANT_NOT_FOUND));

        Set<String> subscribedUsers = chatService.getPrivateSubscribedUserEmails(roomId);

        String other = subscribedUsers.stream()
                .filter(email -> !email.equals(auth.getName()))
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.PRIVATE_CHAT_NEED_RECEIVER));

        saveTimeMark(participant, messageId);

        TimeMark timeMark = ChatConveter.toTimeMark(participant);

        websocketService.queuePrivateMessage(
                other,
                roomId,
                toWsMessage(timeMark, MessageType.READ)
        );
    }
    public void sendGroupLastRead(Long roomId, MessageAuthenticationHeader auth, Long messageId) {

        ChatRoomParticipant participant =
                chatRoomParticipantRepository.findByChatRoom_IdAndMember_IdAndStatus(roomId, auth.getMemberId(), Status.ACTIVE )
                        .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPANT_NOT_FOUND));

        saveTimeMark(participant, messageId);

        TimeMark timeMark = ChatConveter.toTimeMark(participant);
        
        websocketService.topicMessage(
                roomId,
                toWsMessage(timeMark, MessageType.READ)
        );
    }

    @Transactional
    public void saveTimeMark(ChatRoomParticipant participant, Long messageId) {

        participant.setLastReadMessageId(messageId);
        participantRepository.save(participant);
    }
}
