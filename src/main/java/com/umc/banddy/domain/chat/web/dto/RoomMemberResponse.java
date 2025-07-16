package com.umc.banddy.domain.chat.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomMemberResponse {
    private Long userId;
    private String userName;
}

