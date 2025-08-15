package com.umc.banddy.domain.chat.web.dto.chatroom;

import com.umc.banddy.domain.chat.web.dto.PinResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class PinBandChatRoomResponse extends PinResponse {
    private Long bandId;
}
