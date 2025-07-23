package com.umc.banddy.domain.friend.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendRequestResponseDto {
    private Long requestId;
    private Long otherMemberId;
    private String nickname;
    private String email;
    private String bio;
}
