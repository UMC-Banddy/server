package com.umc.banddy.global.apiPayload.exception.handler;

import com.umc.banddy.global.apiPayload.code.BaseErrorCode;
import com.umc.banddy.global.apiPayload.exception.GeneralException;

public class TrackHandler extends GeneralException {
    public TrackHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
