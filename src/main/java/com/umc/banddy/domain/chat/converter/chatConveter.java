package com.umc.banddy.domain.chat.converter;

import com.umc.banddy.domain.chat.web.dto.ChatMessageResponse;

import java.time.LocalDateTime;

public class chatConveter {

    public static ChatMessageResponse toChatMessageResponse(
            Long messageId,
            Long senderId,
            String senderName,
            String content,
            Long roomId,
            LocalDateTime timestamp)
    {
        return ChatMessageResponse.builder()
                .messageId(messageId)
                .senderId(senderId)
                .senderName(senderName)
                .content(content)
                .roomId(roomId)
                .timestamp(timestamp)
                .build();
    }
}
