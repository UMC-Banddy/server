package com.umc.banddy.domain.friend.converter;

import com.umc.banddy.domain.friend.domain.Friend;
import com.umc.banddy.domain.friend.web.dto.FriendResponseDto;
import com.umc.banddy.domain.member.domain.Member;

public class FriendConverter {

    public static FriendResponseDto toDto(Friend friend, Member other) {
        return FriendResponseDto.builder()
                .friendId(friend.getId())
                .otherMemberId(other.getId())
                .nickname(other.getNickname())
                .email(other.getEmail())
                .bio(other.getBio())
                .build();
    }
}
