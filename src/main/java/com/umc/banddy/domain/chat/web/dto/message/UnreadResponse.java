package com.umc.banddy.domain.chat.web.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class UnreadResponse {

    private Long senderId;
    private Long roomId;
    private String content;
    private LocalDateTime timestamp;

}
