package com.umc.banddy.domain.chat.web.dto.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PinBandChatRoomResponse {

    private Long bandId;
    private LocalDateTime pinnedAt;
}
