package com.umc.banddy.domain.chat.web.dto;

import com.umc.banddy.domain.chat.domain.enums.RoomType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.util.Optional;

@Getter
public class ChatMessageRequest {

    @NotNull
    private Long roomId;

    @NotNull
    private RoomType roomType;

    // Optional<T> 선언 (값이 없을 땐 Optional.empty())
    private Optional<@NotBlank Long> receiverId = Optional.empty();

    @NotBlank
    private String content;

    // roomType이 PRIVATE일 땐 receiverId가 반드시 있어야,
    // GROUP일 땐 반드시 없어야 한다는 검증
    @AssertTrue(message = "PRIVATE 채팅엔 receiverId가, GROUP 채팅엔 없어야 합니다.")
    public boolean isReceiverValid() {
        if (roomType == RoomType.PRIVATE) {
            return receiverId.isPresent();
        } else {
            return receiverId.isEmpty();
        }
    }

}