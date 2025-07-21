package com.umc.banddy.domain.chat.web.dto.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class CursorChatMessage {

    private Long messageId; // 메시지 ID
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime timestamp; // 메시지 전송 시간
}
