package com.umc.banddy.domain.friend.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FriendResponseDto {
    private Long friendId;
    private Long otherMemberId;
    private String nickname;
    private String email;
    private String bio;
    private String profileImageUrl;
    private LocalDateTime createdAt;
}
