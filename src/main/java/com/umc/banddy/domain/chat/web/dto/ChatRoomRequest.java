package com.umc.banddy.domain.chat.web.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatRoomRequest {

    private List<Long> memberIds;
    private String imageUrl;
    private String roomName;
}
