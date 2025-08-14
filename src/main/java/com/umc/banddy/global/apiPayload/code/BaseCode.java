package com.umc.banddy.global.apiPayload.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {

    ReasonDTO getReason();

    ReasonDTO getReasonHttpStatus();

    String getCode();

    String getMessage();

    HttpStatus getHttpStatus();
}
