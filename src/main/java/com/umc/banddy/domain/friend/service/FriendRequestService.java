package com.umc.banddy.domain.friend.service;

import com.umc.banddy.domain.friend.web.dto.FriendResponseDto;

import java.util.List;

public interface FriendRequestService {

    void requestFriend(Long requesterId, Long receiverId);

    void acceptFriend(Long requestId);

    void rejectFriend(Long requestId);

    List<FriendResponseDto> getReceivedFriendRequests(Long memberId);

    FriendResponseDto getFriendRequestDetail(Long requestId);
}
