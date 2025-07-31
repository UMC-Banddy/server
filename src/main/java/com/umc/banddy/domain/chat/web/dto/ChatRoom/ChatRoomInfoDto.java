package com.umc.banddy.domain.chat.web.dto.ChatRoom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class ChatRoomInfoDto extends ChatRoomInfo{

    private Long roomId;
    private List<MemberInfo> memberInfos;

}
