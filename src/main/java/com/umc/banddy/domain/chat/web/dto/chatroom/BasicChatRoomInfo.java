package com.umc.banddy.domain.chat.web.dto.chatroom;

import com.umc.banddy.domain.chat.web.dto.message.CursorChatMessageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class BasicChatRoomInfo {
    private Long roomId;
    private CursorChatMessageResponse messageList;
    private ParticipantInfos participantInfos;
}
