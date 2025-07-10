package com.umc.banddy.global.apiPayload.exception.handler;

import com.umc.banddy.global.apiPayload.code.BaseErrorCode;
import com.umc.banddy.global.apiPayload.exception.GeneralException;

public class FolderHandler extends GeneralException {
    public FolderHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
