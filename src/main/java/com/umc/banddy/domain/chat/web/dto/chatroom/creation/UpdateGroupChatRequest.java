package com.umc.banddy.domain.chat.web.dto.chatroom.creation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@Builder
public class UpdateGroupChatRequest {

    private Long roomId;
    private String roomName;
}
