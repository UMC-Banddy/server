package com.umc.banddy.domain.friend.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendResponseDto {
    private Long friendId;
    private String nickname;
    private String email;
}
