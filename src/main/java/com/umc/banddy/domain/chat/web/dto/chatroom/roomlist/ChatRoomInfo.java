package com.umc.banddy.domain.chat.web.dto.chatroom.roomlist;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public abstract class ChatRoomInfo {


    private String roomType;

    private Long unreadCount;

    private LocalDateTime pinnedAt;

    private LocalDateTime lastMessageAt;
}
