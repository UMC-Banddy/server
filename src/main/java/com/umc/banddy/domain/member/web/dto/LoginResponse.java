package com.umc.banddy.domain.member.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private Long memberId;
    private String accessToken;
    private String refreshToken;

}
