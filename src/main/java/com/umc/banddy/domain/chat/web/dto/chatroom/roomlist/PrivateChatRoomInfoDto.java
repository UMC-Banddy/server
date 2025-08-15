package com.umc.banddy.domain.chat.web.dto.chatroom.roomlist;

import com.umc.banddy.domain.chat.web.dto.chatroom.MemberInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class PrivateChatRoomInfoDto extends ChatRoomInfo {

    private Long roomId;

    private String chatName;

    private String imageUrl;
    private MemberInfo memberInfo;

}
