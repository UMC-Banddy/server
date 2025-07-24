package com.umc.banddy.domain.chat.web.dto.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ChatSystemResponse {
    private Long roomId;
    private String message;
    private LocalDateTime timestamp;
}
