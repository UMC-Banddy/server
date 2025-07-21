package com.umc.banddy.domain.chat.web.dto.Message;

import com.umc.banddy.domain.chat.domain.enums.RoomType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class ChatMessageRequest {

    @NotNull(message = "roomType은 필수입니다.")
    private RoomType roomType;

    // Optional 대신 그냥 Long: GROUP이면 null, PRIVATE면 not null
    private Long receiverId;

    @NotBlank(message = "content는 공백일 수 없습니다.")
    private String content;

    // roomType이 PRIVATE일 땐 receiverId가 반드시 not-null
    // roomType이 GROUP일 땐 receiverId가 반드시 null
    @AssertTrue
    public boolean isReceiverValid() {
        if (roomType == RoomType.PRIVATE) {
            return receiverId != null;
        } else {
            return receiverId == null;
        }
    }

}