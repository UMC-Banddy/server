package com.umc.banddy.domain.chat.converter;

import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.web.dto.ChatMessageResponse;

import java.time.LocalDateTime;

public class chatConveter {

    public static ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage){
        return ChatMessageResponse.builder()
                .messageId(chatMessage.getId())
                .senderId(chatMessage.getMember().getId())
                .senderName(chatMessage.getMember().getNickname())
                .content(chatMessage.getContent())
                .roomId(chatMessage.getChatRoom().getId())
                .timestamp(chatMessage.getCreatedAt())
                .build();
    }
}
