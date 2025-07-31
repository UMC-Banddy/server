package com.umc.banddy.domain.chat.web.dto.chatRoom.roomList;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public abstract class ChatRoomInfo {

    private String chatName;

    private String imageUrl;

    private Long unreadCount;

    private LocalDateTime lastMessageAt;
}
