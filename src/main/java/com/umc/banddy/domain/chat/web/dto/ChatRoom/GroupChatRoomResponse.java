 package com.umc.banddy.domain.chat.web.dto.ChatRoom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

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
