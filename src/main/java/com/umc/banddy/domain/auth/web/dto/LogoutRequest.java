package com.umc.banddy.domain.auth.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LogoutRequest {

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
}
