package com.umc.banddy.domain.chat.web.dto.chatRoom.roomList;

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
