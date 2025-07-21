package com.umc.banddy.domain.chat.web.dto.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CursorChatMessageResponse {

    private Long roomId;
    private List<CursorChatMessage> messages;
    private boolean hasNext;
    private Long lastMessageId;
}
