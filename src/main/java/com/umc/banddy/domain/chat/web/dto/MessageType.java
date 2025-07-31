package com.umc.banddy.domain.chat.web.dto;

public enum MessageType {
    MESSAGE, // 일반 메시지
    JOIN, // 입장 메시지
    LEAVE, // 퇴장 메시지
    SYSTEM, // 시스템 메시지
    MARk_AS_READ, // 읽음 표시 메시지
    MARK_AS_UNREAD, // 읽지 않음 표시 메시지
    UNREAD_MESSAGE
}
