package com.umc.banddy.domain.chat.web.dto.message;

import com.umc.banddy.domain.chat.domain.enums.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PrivateChatMessageRequest {

    @NotNull(message = "roomType은 필수입니다.")
    private RoomType roomType;

    @NotNull(message = "receiverId은 필수입니다.")
    private Long receiverId;

    @NotBlank(message = "content는 공백일 수 없습니다.")
    private String content;

}
