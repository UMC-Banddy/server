package com.umc.banddy.domain.auth.web.dto;

import lombok.Getter;

@Getter
public class DeactivateRequest {
    private Long memberId;
    private String refreshToken;
}
