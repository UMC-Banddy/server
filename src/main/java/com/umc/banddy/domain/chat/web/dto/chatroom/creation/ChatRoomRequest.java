package com.umc.banddy.domain.chat.web.dto.chatroom.creation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ChatRoomRequest {
//    @Schema(
//            description = "업데이트할 프로필 이미지(선택)",
//            type        = "string",
//            format      = "binary"
//    )
//    private MultipartFile image;
    private List<Long> memberIds;
    private String roomName;
}
