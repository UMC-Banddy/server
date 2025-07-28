package com.umc.banddy.domain.chat.service;


import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.chat.domain.enums.Role;
import com.umc.banddy.domain.chat.repository.ChatMessageRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomParticipantRepository;
import com.umc.banddy.domain.chat.repository.ChatCustomRepository;
import com.umc.banddy.domain.chat.web.dto.Message.*;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.umc.banddy.domain.chat.converter.ChatConveter.toChatMessageResponse;


@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final WebsocketService websocketService;
    private final ChatService chatService;
    private final ChatCustomRepository chatCustomRepository;
    private final ChatRoomParticipantCache participantCachecache;

    // 채팅 메세지 저장
    @Transactional
    public ChatMessage saveMessage(ChatRoom chatRoom, Member member, ChatMessageRequest messageRequest){
        ChatMessage chatMessage = ChatMessage.builder()
                .member(member)
                .chatRoom(chatRoom)
                .content(messageRequest.getContent())
                .build();
        System.out.println("메세지 저장"+ chatMessage.getId());

        return chatMessageRepository.save(chatMessage);
    }

    // 채팅 응답 반환
    public ChatMessageResponse chatToResponse(ChatMessage chatMessage) {return toChatMessageResponse(chatMessage);}

    @Transactional
    public ChatSystemResponse exitChatRoom(ChatRoom chatRoom, Member member){
        ChatRoomParticipant chatRoomParticipant
                = participantRepository
                .findTopByChatRoomAndMemberAndStatusOrderByIdDesc(chatRoom, member, Status.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("참여하지 않은 채팅방입니다."));
        chatRoomParticipant.setStatus(Status.INACTIVE);

        ChatMessage chatMessage = ChatMessage.builder()
                .member(member)
                .chatRoom(chatRoom)
                .content(member.getNickname() + "님이 채팅방을 나갔습니다.")
                .build();

        chatMessageRepository.save(chatMessage);
        participantRepository.save(chatRoomParticipant);

        participantCachecache.removeParticipant(chatRoom.getId(), member.getEmail());

        websocketService.topicMessage(
                chatRoom.getId(),
                ChatMessageResponse.builder()
                        .roomId(chatRoom.getId())
                        .content(chatMessage.getContent())
                        .senderId(member.getId())
                        .senderName(null)
                        .build()
        );



        return ChatSystemResponse.builder()
                .roomId(chatMessage.getId())
                .message(chatMessage.getContent())
                .build();
    }

    @Transactional
    public ChatSystemResponse joinChatRoom(ChatRoom chatRoom, Member member){

        if (participantRepository.findTopByChatRoomAndMemberAndStatusOrderByIdDesc(chatRoom, member, Status.ACTIVE).isPresent()) {
            throw new IllegalStateException("이미 참여 중인 채팅방입니다.");
        }

        ChatRoomParticipant participant = ChatRoomParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .role(Role.MEMBER) // 기본 역할 설정
                .status(Status.ACTIVE) // 기본 상태 설정
                .lastReadAt(LocalDateTime.now()) // 초기값 설정
                .build();

        ChatMessage chatMessage = ChatMessage.builder()
                .member(member)
                .chatRoom(chatRoom)
                .content(member.getNickname() + "님이 채팅방에 참여하셨습니다.")
                .build();

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

        participantRepository.save(participant);
        chatMessageRepository.save(chatMessage);;

        participantCachecache.addParticipant(chatRoom.getId(), member.getEmail());

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
}
