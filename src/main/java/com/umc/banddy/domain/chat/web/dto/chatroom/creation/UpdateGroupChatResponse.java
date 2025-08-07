package com.umc.banddy.domain.chat.web.dto.chatroom.creation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdateGroupChatResponse {

    private Long roomId;
    private String roomName;
    private String roomProfileUrl;

}
