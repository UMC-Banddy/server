package com.umc.banddy.domain.auth.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailVerifyResponse {
    private boolean verified;
    private String message;
}
