package com.umc.banddy.domain.friend.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequestDto {
    private Long targetMemberId;
    private String message;
}
