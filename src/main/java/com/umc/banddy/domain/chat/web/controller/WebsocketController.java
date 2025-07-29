package com.umc.banddy.domain.chat.web.controller;


import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.chat.domain.enums.RoomType;
import com.umc.banddy.domain.chat.service.ChatMessageService;
import com.umc.banddy.domain.chat.service.ChatService;
import com.umc.banddy.domain.chat.service.WebsocketService;
import com.umc.banddy.domain.chat.web.dto.Message.ChatMessageRequest;
import com.umc.banddy.domain.chat.web.dto.Message.ChatMessageResponse;
import com.umc.banddy.domain.chat.web.dto.MessageAuthenticationHeader;
import com.umc.banddy.domain.chat.web.dto.MessageType;
import com.umc.banddy.domain.chat.web.dto.TimeMark;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Optional;

import static com.umc.banddy.domain.chat.converter.ChatConveter.toTimeMark;
import static com.umc.banddy.domain.chat.converter.ChatConveter.toWsMessage;

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
            @DestinationVariable Long roomId
    ) {
        MessageAuthenticationHeader auth = (MessageAuthenticationHeader) principal;
        chatMessageService.sendChatMessage(roomId, auth.getMemberId(),messageRequest);
    }

    // 채팅방 구독
    @MessageMapping("/chat/subscribe/{roomId}")
    public void subscribeChatRoom(
            Principal principal,
            @DestinationVariable Long roomId
    ) {
        MessageAuthenticationHeader auth = (MessageAuthenticationHeader) principal;
        Pair<ChatRoom, Member> pair = chatService.verifedChatRoomAndMember(roomId, auth.getMemberId());
        ChatRoom chatRoom = pair.getLeft();
        Member member = pair.getRight();
        websocketService.topicMessage(
                chatRoom.getId(),
                toWsMessage(toTimeMark(chatRoom,member), MessageType.MARk_AS_READ)
        );
    }

    // 채팅방 구독 해제
    @MessageMapping("/chat/unsubscribe/{roomId}")
    public void unsubscribeChatRoom(
            @AuthenticationPrincipal MessageAuthenticationHeader principal,
            @DestinationVariable Long roomId
    ) {
        Pair<ChatRoom, Member> pair = chatService.verifedChatRoomAndMember(roomId, principal.getMemberId());

        ChatRoom chatRoom = pair.getLeft();
        Member member = pair.getRight();

        // 구독 해제 시점 갱신
        ChatRoomParticipant participant = chatService.markLastRead(chatRoom,member);

        TimeMark timeMark = TimeMark.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .roomId(chatRoom.getId())
                .timestamp(participant.getLastReadAt())
                .build();

        websocketService.topicMessage(
                chatRoom.getId(),
                toWsMessage(timeMark, MessageType.MARK_AS_UNREAD)
        );
    }

}
