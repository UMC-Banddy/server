package com.umc.banddy.domain.chat.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class TimeMark {
    private Long memberId;
    private String nickname;
    private Long roomId;
    private Long messageId;
}
