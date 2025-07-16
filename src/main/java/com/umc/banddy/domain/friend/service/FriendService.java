package com.umc.banddy.domain.friend.service;

import com.umc.banddy.domain.friend.web.dto.FriendRequestDto;
import com.umc.banddy.domain.friend.web.dto.FriendResponseDto;

import java.util.List;

public interface FriendService {

    void requestFriend(Long requesterId, Long receiverId);

    void acceptFriend(Long friendId);

    void rejectFriend(Long friendId);

    void deleteFriend(Long friendId);

    List<FriendResponseDto> getMyFriends(Long memberId);
    // 받은 친구 요청 목록 조회
    List<FriendResponseDto> getReceivedFriendRequests(Long memberId);

    // 특정 친구 요청 상세 조회
    FriendResponseDto getFriendRequestDetail(Long friendId);

}
