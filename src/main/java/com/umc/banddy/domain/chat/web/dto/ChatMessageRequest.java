package com.umc.banddy.domain.chat.web.dto;

import com.umc.banddy.domain.chat.domain.enums.RoomType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ChatMessageRequest {

    @NotBlank
    private Long SenderId; // 메시지를 보낸 사람의 ID, 테스트용, 나중에는 jwt로 대체 예정

    @NotBlank
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @NotBlank
    private String content; // 문자열만 테스트용, 나중에는 몽고 DB로 다양한 타입 받을 예정
    //private String type;    // 메시지 타입 (예: TEXT, IMAGE 등)
}
