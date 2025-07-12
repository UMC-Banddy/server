package com.umc.banddy.domain.chat.web.controller;


import com.umc.banddy.domain.chat.entity.ChatMessage;
import com.umc.banddy.domain.chat.entity.ChatRoom;
import com.umc.banddy.domain.chat.entity.ChatRoomParticipant;
import com.umc.banddy.domain.chat.entity.enums.RoomType;
import com.umc.banddy.domain.chat.repository.ChatMessageRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomParicipantRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomRepository;
import com.umc.banddy.domain.chat.service.ChatService;
import com.umc.banddy.domain.chat.web.dto.ChatMessageRequest;
import com.umc.banddy.domain.chat.web.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/sendMessage/{roomId}")
    public void sendMessage(
            //Principal principal, // 나중에 사용자 인증정보 활용시 추가
            @Payload ChatMessageRequest messageRequest,
            @Validated @DestinationVariable Long roomId
    ) {

        // 유효 참여자, 유효 채팅방인지 검증하는 로직 추가할것

        // 채팅 메세지 저장
        ChatMessage chatMessage = chatService.saveMessage(messageRequest, roomId);

        // 응답 생성
        ChatMessageResponse chatMessageResponse = chatService.chatToResponse(chatMessage);

        // 전송 로직 분기
        if (messageRequest.getRoomType().equals(RoomType.GROUP)) {
            // GROUP: 토픽 브로드캐스트
            messagingTemplate.convertAndSend(
                    "/topic/rooms/" + roomId,
                    chatMessageResponse
            );
        }

        // 단일은 다시 해야할듯
//        } else if (messageRequest.getRoomType().equals(RoomType.PRIVATE)
//                || messageRequest.getRoomType().equals(RoomType.BAND)) {
//            // PRIVATE or BAND: 단일 사용자에게 전송
//            Long recipientId = messageRequest.getRecipientId();
//            messagingTemplate.convertAndSendToUser(
//                    recipientId.toString(),
//                    "/queue/rooms/" + roomId,
//                    chatMessage
//            );
    }

//    @MessageMapping("/chat/addUser/{roomId}")
//    public void addUser(
//            //Principal principal, // 나중에 사용자 인증정보 활용시 추가
//            @Payload ChatMessageRequest messageRequest,
//            @DestinationVariable Long roomId
//    ) {
//        return;
//    }

}
