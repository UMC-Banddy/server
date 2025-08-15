package com.umc.banddy.domain.chat.web.dto.chatroom.roomlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ChatRoomListResponse {

    private List<ChatRoomInfo> chatRoomInfos;

}
