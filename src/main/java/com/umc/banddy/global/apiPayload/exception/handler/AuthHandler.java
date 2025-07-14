package com.umc.banddy.global.apiPayload.exception.handler;

import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import lombok.Getter;

@Getter
public class AuthHandler extends RuntimeException {
    private final ErrorStatus errorStatus;

    public AuthHandler(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }
}