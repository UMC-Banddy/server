package com.umc.banddy.domain.chat.web.dto.chatRoom.creation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ChatRoomRequest {

    private List<Long> memberIds;
    private String imageUrl;
    private String roomName;
}
