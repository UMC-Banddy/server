package com.umc.banddy.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailVerifyRequest {

    @NotBlank
    private String code;
}
