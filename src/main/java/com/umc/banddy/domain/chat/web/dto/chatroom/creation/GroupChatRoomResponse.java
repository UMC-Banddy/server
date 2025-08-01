 package com.umc.banddy.domain.chat.web.dto.chatroom.creation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

 @Getter
@AllArgsConstructor
@Builder
public class GroupChatRoomResponse {

    private Long roomId;

    private String bandName;

    private String bandProfileUrl;

    private Long managerId;

    private String managerName;

    private String managerProfileUrl;
}
