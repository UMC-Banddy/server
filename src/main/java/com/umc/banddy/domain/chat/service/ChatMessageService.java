package com.umc.banddy.domain.chat.service;


import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.chat.domain.enums.Role;
import com.umc.banddy.domain.chat.domain.enums.RoomType;
import com.umc.banddy.domain.chat.repository.ChatMessageRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomParticipantRepository;
import com.umc.banddy.domain.chat.repository.ChatCustomRepository;
import com.umc.banddy.domain.chat.web.dto.Message.*;
import com.umc.banddy.domain.chat.web.dto.MessageType;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Status;
import com.umc.banddy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    // 채팅 메세지 저장
    @Transactional
    public ChatMessage saveMessage(ChatRoom chatRoom, Member member, ChatMessageRequest messageRequest){
        ChatMessage chatMessage = ChatMessage.builder()
                .member(member)
                .chatRoom(chatRoom)
                .content(messageRequest.getContent())
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
                .findTopByChatRoomAndMemberAndStatusOrderByIdDesc(chatRoom, member, Status.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("참여하지 않은 채팅방입니다."));

        chatRoomParticipant.setStatus(Status.INACTIVE);
        participantRepository.save(chatRoomParticipant);

        ChatMessage chatMessage = ChatMessage.builder()
                .member(member)
                .chatRoom(chatRoom)
                .content(member.getNickname() + "님이 채팅방을 나갔습니다.")
                .build();

        chatMessageRepository.save(chatMessage);

        websocketService.topicMessage(
                chatRoom.getId(),
                ChatMessageResponse.builder()
                        .messageId(chatMessage.getId())
                        .roomId(chatRoom.getId())
                        .content(chatMessage.getContent())
                        .senderId(member.getId())
                        .senderName("System")
                        .timestamp(chatMessage.getCreatedAt())
                        .build()
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
                        p.setLastReadAt(LocalDateTime.now());
                    } else if (p.getStatus() == Status.ACTIVE) {throw new IllegalArgumentException("이미 채팅에 참여중입니다");}
                    return p;
                }).orElseGet(() -> ChatRoomParticipant.builder()
                        .chatRoom(chatRoom)
                        .member(member)
                        .role(Role.MEMBER)
                        .status(Status.ACTIVE)
                        .lastReadAt(LocalDateTime.now())
                        .build());

        participantRepository.save(participant);

        ChatMessage chatMessage = ChatMessage.builder()
                .member(member)
                .chatRoom(chatRoom)
                .content(member.getNickname() + "님이 채팅방에 참여하셨습니다.")
                .build();

        chatMessageRepository.save(chatMessage);;

        websocketService.topicMessage(
                chatRoom.getId(),
                ChatMessageResponse.builder()
                        .messageId(chatMessage.getId())
                        .roomId(chatRoom.getId())
                        .content(chatMessage.getContent())
                        .senderId(member.getId())
                        .senderName("System")
                        .timestamp(chatMessage.getCreatedAt())
                        .build()
        );

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
                .roomId(roomId)
                .messages(ccm)
                .hasNext(ccm.size() == limit)
                .lastMessageId(ccm.isEmpty() ? null : ccm.get(ccm.size() - 1).getMessageId())
                .build();

    }
    public void sendChatMessage(Long roomId, Long userId, ChatMessageRequest messageRequest) {
        //System.out.println("메세지 전송로직");
        String destination = "/topic/room/" + roomId;
        Pair<ChatRoom, Member> pair = chatService.verifedChatRoomAndMember(roomId, userId);

        ChatRoom chatRoom = pair.getLeft();
        Member member = pair.getRight();

        // 채팅 메세지 저장
        ChatMessage chatMessage = saveMessage(chatRoom,member, messageRequest);

        Set<String> allParticipants =  participantCache.getParticipants(roomId); // 이메일 기준
        Set<String> subscribedUsers = chatService.getSubscribedUserEmails(roomId);

        // 성능상 개선 여지 있음
        Set<String> unsubscribedUsers = new HashSet<>(allParticipants);
        System.out.println("roomid 참여자"+allParticipants);
        System.out.println("세션에 구독정보"+subscribedUsers);
        unsubscribedUsers.removeAll(subscribedUsers);
        System.out.println("차집합 정보"+unsubscribedUsers);

        RoomType roomType = chatRoom.getRoomType();

        // 전송 로직 분기
        // roomType 검증에 대해서는 db 검증을 거칠지, 메세지에서 첨부된 값을 신뢰할지 고민이 필요
        if (roomType.equals(RoomType.GROUP)) {
            // GROUP: 토픽 브로드캐스트
            ChatMessageResponse chatMessageResponse = chatToResponse(chatMessage);
            websocketService.topicMessage(roomId, toWsMessage(chatMessageResponse, MessageType.MESSAGE));
            // 비구독자 처리
            UnreadResponse unreadResponse = UnreadResponse.builder()
                    .senderId(member.getId())
                    .roomId(chatRoom.getId())
                    .content(chatMessage.getContent())
                    .build();
            for (String email : allParticipants) {
                if (unsubscribedUsers.contains(email)) {
                    System.out.println("비구독자"+email);
                    websocketService.queueUnreadMessage(email, toWsMessage(unreadResponse, MessageType.MARK_AS_UNREAD));
                }
            }
        } else if (roomType.equals(RoomType.PRIVATE) || roomType.equals(RoomType.BAND)) {
            // PRIVATE, BAND: 세션 단위로 유저에게 개별 전송
            Long receiverId = Optional.ofNullable(messageRequest.getReceiverId())
                    .orElseThrow(() -> new IllegalArgumentException("receiverId가 필요합니다."));
            // 캐싱 고려
            String receiverEmail = memberRepository.findEmailById(receiverId);
            if (unsubscribedUsers.contains(receiverEmail)) {
                ChatMessageResponse chatMessageResponse = chatToResponse(chatMessage);
                websocketService.queuePrivateMessage(receiverEmail, roomId, toWsMessage(chatMessageResponse, MessageType.MESSAGE));
            }else{
                UnreadResponse unreadResponse = UnreadResponse.builder()
                        .senderId(member.getId())
                        .roomId(chatRoom.getId())
                        .content(chatMessage.getContent())
                        .build();
                websocketService.queueUnreadMessage(receiverEmail, toWsMessage(unreadResponse, MessageType.MARK_AS_UNREAD));
            }
        }
    }

}
