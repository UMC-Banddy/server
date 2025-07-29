package com.umc.banddy.domain.chat.web.dto.ChatRoom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ChatRoomInfo {

    private String chatName;

    private String imageUrl;

    private List<MemberInfo> memberInfos;

    private Long unreadCount;

    private LocalDateTime lastMessageAt;

}
