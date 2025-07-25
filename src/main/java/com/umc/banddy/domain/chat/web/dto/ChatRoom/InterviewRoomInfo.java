package com.umc.banddy.domain.chat.web.dto.ChatRoom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class InterviewRoomInfo {

    private Long memberId;

    private String profileImageUrl;

    private String session;

    private LocalDateTime lastMessageAt;

    private Long unreadCount;
}
