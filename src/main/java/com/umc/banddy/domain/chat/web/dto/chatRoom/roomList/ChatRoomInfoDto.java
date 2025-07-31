package com.umc.banddy.domain.chat.web.dto.chatRoom.roomList;

import com.umc.banddy.domain.chat.web.dto.chatRoom.MemberInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class ChatRoomInfoDto extends ChatRoomInfo {

    private Long roomId;
    private List<MemberInfo> memberInfos;

}
