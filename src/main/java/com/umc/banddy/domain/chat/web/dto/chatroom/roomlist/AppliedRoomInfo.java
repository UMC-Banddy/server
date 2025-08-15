package com.umc.banddy.domain.chat.web.dto.chatroom.roomlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AppliedRoomInfo {

    private Long bandId;

    private Long roomId;

    private String bandName;

    private String profileImageUrl;

    private LocalDateTime lastMessageAt;

    private Long unreadCount;

}
