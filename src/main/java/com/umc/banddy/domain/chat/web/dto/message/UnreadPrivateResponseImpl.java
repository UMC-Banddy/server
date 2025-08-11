package com.umc.banddy.domain.chat.web.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class UnreadPrivateResponseImpl implements UnreadResponse {

    private Long senderId;
    private Long roomId;
    private String content;
    private LocalDateTime timestamp;

}
