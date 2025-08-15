package com.umc.banddy.domain.chat.web.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@SuperBuilder
public class UnreadResponseImpl implements UnreadResponse {
    private Long senderId;
    private Long roomId;
    private Long bandId;
    private String content;
    private LocalDateTime timestamp;

}
