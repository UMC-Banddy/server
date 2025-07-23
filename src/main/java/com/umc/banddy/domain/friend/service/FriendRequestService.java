package com.umc.banddy.domain.friend.service;

import com.umc.banddy.domain.friend.web.dto.FriendRequestResponseDto;

import java.util.List;

public interface FriendRequestService {

    void requestFriend(Long requesterId, Long receiverId);

    void acceptFriend(Long requestId);

    void rejectFriend(Long requestId);

    List<FriendRequestResponseDto> getReceivedFriendRequests(Long memberId);

    FriendRequestResponseDto getFriendRequestDetail(Long requestId);
}
