package com.umc.banddy.domain.friend.service;

import com.umc.banddy.domain.friend.web.dto.FriendRequestDto;
import com.umc.banddy.domain.friend.web.dto.FriendResponseDto;

import java.util.List;

public interface FriendService {
    void deleteFriend(Long friendId);
    List<FriendResponseDto> getMyFriends(Long memberId);
}

