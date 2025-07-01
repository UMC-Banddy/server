package com.umc.banddy.global.apiPayload.exception.handler;

import com.umc.banddy.global.apiPayload.code.BaseErrorCode;
import com.umc.banddy.global.apiPayload.exception.GeneralException;

public class TempHandler extends GeneralException {

    public TempHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}