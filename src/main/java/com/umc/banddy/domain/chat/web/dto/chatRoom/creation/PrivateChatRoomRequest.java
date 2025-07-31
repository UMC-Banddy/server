package com.umc.banddy.domain.chat.web.dto.chatRoom.creation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PrivateChatRoomRequest {

    private Long memberId;
}
