package com.umc.banddy.domain.chat.web.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    private Long messageId; // 메시지 ID
    private Long senderId;
    private String senderName;
    private String content; // 메세지 내용, 나중에는 몽고 DB로 다양한 타입 받을 예정
    //private String type; // 메시지 타입 (예: TEXT, IMAGE 등)
    private Long roomId; // 메시지가 속한 채팅방 ID
    private LocalDateTime timestamp; // 메시지 전송 시간
}
