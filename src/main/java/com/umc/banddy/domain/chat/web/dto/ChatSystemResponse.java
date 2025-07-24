package com.umc.banddy.domain.chat.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ChatSystemResponse {
    private Long roomId;
    private String message;
}
