package com.umc.banddy.domain.chat.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class chatRequestRequest {
    private Long targetMemberId;
    private String message;
}
