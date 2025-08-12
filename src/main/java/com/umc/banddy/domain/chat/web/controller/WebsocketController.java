package com.umc.banddy.domain.chat.web.controller;


import com.umc.banddy.domain.chat.service.ChatMessageService;
import com.umc.banddy.domain.chat.service.ChatService;
import com.umc.banddy.domain.chat.service.WebsocketService;
import com.umc.banddy.domain.chat.web.dto.message.ChatMessageRequest;
import com.umc.banddy.domain.chat.web.dto.MessageAuthenticationHeader;
import com.umc.banddy.domain.chat.web.dto.message.GroupChatMessageRequest;
import com.umc.banddy.domain.chat.web.dto.message.PrivateChatMessageRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebsocketController {

    private final ChatService chatService;
    private final ChatMessageService chatMessageService;
    private final WebsocketService websocketService;

    @MessageMapping("/chat/sendMessage/{roomId}")
    public void sendMessage(
            Principal principal,
            @Valid @Payload ChatMessageRequest messageRequest,
            @NotNull @Positive @DestinationVariable Long roomId
    ) {
        MessageAuthenticationHeader auth = (MessageAuthenticationHeader) principal;
        chatMessageService.sendChatMessage(roomId, auth.getMemberId(),messageRequest);
    }

//    // 채팅방 구독
//    @MessageMapping("/chat/subscribe/{roomId}")
//    public void subscribeChatRoom(
//            Principal principal,
//            @NotNull @Positive @DestinationVariable Long roomId
//    ) {
//        MessageAuthenticationHeader auth = (MessageAuthenticationHeader) principal;
//        Pair<ChatRoom, Member> pair = chatService.verifedChatRoomAndMember(roomId, auth.getMemberId());
//        ChatRoom chatRoom = pair.getLeft();
//        Member member = pair.getRight();
//        websocketService.topicMessage(
//                chatRoom.getId(),
//                toWsMessage(toTimeMark(chatRoom,member), MessageType.MARk_AS_READ)
//        );
//    }
//
//    // 채팅방 구독 해제
//    @MessageMapping("/chat/unsubscribe/{roomId}")
//    public void unsubscribeChatRoom(
//            @AuthenticationPrincipal MessageAuthenticationHeader principal,
//            @NotNull @Positive @DestinationVariable Long roomId
//    ) {
//        Pair<ChatRoom, Member> pair = chatService.verifedChatRoomAndMember(roomId, principal.getMemberId());
//
//        ChatRoom chatRoom = pair.getLeft();
//        Member member = pair.getRight();
//
//        // 구독 해제 시점 갱신
//        ChatRoomParticipant participant = chatService.markLastRead(chatRoom,member);
//
//        TimeMark timeMark = TimeMark.builder()
//                .memberId(member.getId())
//                .nickname(member.getNickname())
//                .roomId(chatRoom.getId())
//                .timestamp(participant.getLastReadAt())
//                .build();
//
//        websocketService.topicMessage(
//                chatRoom.getId(),
//                toWsMessage(timeMark, MessageType.MARK_AS_UNREAD)
//        );
//    }


    // 그룹 메세지 전송
    @MessageMapping("/chat/private.sendMessage/{roomId}")
    public void sendPrivateMessage(
            Principal principal,
            @Valid @Payload PrivateChatMessageRequest messageRequest,
            @NotNull @Positive @DestinationVariable Long roomId
    ) {
        MessageAuthenticationHeader auth = (MessageAuthenticationHeader) principal;
        chatMessageService.sendPrivateMessage(roomId, auth, messageRequest);
    }

    // 개인 메세지 전송
    @MessageMapping("/chat/group.sendMessage/{roomId}")
    public void sendGroupMessage(
            Principal principal,
            @Valid @Payload GroupChatMessageRequest messageRequest,
            @NotNull @Positive @DestinationVariable Long roomId
    ) {
        MessageAuthenticationHeader auth = (MessageAuthenticationHeader) principal;
        chatMessageService.sendGroupMessage(roomId, auth, messageRequest);
    }

    // 개인 타임마크 전송
    @MessageMapping("/chat/private.lastRead/{roomId}")
    public void sendPrivateLastRead(
            Principal principal,
            @NotNull @Positive Long messageId,
            @NotNull @Positive @DestinationVariable Long roomId
    ) {
        MessageAuthenticationHeader auth = (MessageAuthenticationHeader) principal;
        System.out.println("Participant ID: " + auth.getName());
        chatMessageService.sendPrivateLastRead(roomId, auth, messageId);
    }

    // 그룹 타임마크 전송
    @MessageMapping("/chat/group.lastRead/{roomId}")
    public void sendGroupLastRead(
            Principal principal,
            @NotNull @Positive Long messageId,
            @NotNull @Positive @DestinationVariable Long roomId
    ) {
        MessageAuthenticationHeader auth = (MessageAuthenticationHeader) principal;
        chatMessageService.sendGroupLastRead(roomId, auth, messageId);
    }

}
