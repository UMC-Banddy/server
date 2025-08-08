package com.umc.banddy.domain.chat.web.dto.chatroom.creation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BandJoinRequest {


    @Schema(
            description = "지원 세션, 중복 안됨",
            type = "string",
            allowableValues = {"🎤 보컬 🎤",
            "🎸 일렉 기타 🎸",
            "🪕 어쿠스틱 기타 🪕",
            "🎵 베이스 🎵",
            "🥁 드럼 🥁",
            "🎹 키보드 🎹",
            "🎻 바이올린 🎻",
            "🎺 트럼펫 🎺"},
            example = "🎸 일렉 기타 🎸"
    )
    private String session;
}
