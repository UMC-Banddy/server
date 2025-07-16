package com.umc.banddy.domain.chat.web.controller;


import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.domain.enums.RoomType;
import com.umc.banddy.domain.chat.service.ChatService;
import com.umc.banddy.domain.chat.web.dto.ChatMessageRequest;
import com.umc.banddy.domain.chat.web.dto.ChatMessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebsocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/sendMessage/{roomId}")
    public void sendMessage(
            Principal principal,
            @Valid @Payload ChatMessageRequest messageRequest,
            @DestinationVariable Long roomId
    ) {

        // 채팅 메세지 저장
        ChatMessage chatMessage = chatService.saveMessage(principal, messageRequest, roomId);

        // 응답 생성
        ChatMessageResponse chatMessageResponse = chatService.chatToResponse(chatMessage);

        // 전송 로직 분기
        // 그룹 채팅
        // roomType 검증에 대해서는 db 검증을 거칠지, 메세지에서 첨부된 값을 신뢰할지 고민이 필요
        if (messageRequest.getRoomType().equals(RoomType.GROUP)) {
            // GROUP: 토픽 브로드캐스트
            messagingTemplate.convertAndSend(
                    "/topic/rooms/" + roomId,
                    chatMessageResponse
            );
        } else if (messageRequest.getRoomType().equals(RoomType.PRIVATE)
                || messageRequest.getRoomType().equals(RoomType.BAND)) {

            Long receiverId = messageRequest.getReceiverId().orElseThrow(
                    () -> new IllegalArgumentException("수신자 ID가 필요합니다.")
            );
            // PRIVATE, BAND: 세션 단위로 유저에게 개별 전송
            messagingTemplate.convertAndSendToUser(
                    chatService.findReceiverEmail(receiverId),
                    "/queue/rooms/" + roomId,
                    chatMessageResponse
            );
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
}
