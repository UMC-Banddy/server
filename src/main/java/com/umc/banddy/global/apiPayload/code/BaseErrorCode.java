package com.umc.banddy.global.apiPayload.code;

public interface BaseErrorCode {

    ErrorReasonDTO getReason();

    ErrorReasonDTO getReasonHttpStatus();

    String getCode();

    String getMessage();

    org.springframework.http.HttpStatus getHttpStatus();
}
