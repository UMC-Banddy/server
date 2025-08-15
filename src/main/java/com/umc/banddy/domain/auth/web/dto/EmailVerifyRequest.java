package com.umc.banddy.domain.auth.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailVerifyRequest {
    private String email;
    @NotBlank
    private String code;
}
