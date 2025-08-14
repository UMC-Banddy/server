package com.umc.banddy.domain.chat.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@SuperBuilder
public class PinResponse {
    private LocalDateTime pinnedAt;
}
