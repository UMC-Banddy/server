package com.umc.banddy.domain.friend.repository;

import com.umc.banddy.domain.friend.domain.FriendRequest;
import com.umc.banddy.domain.friend.domain.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    Optional<FriendRequest> findTopByRequesterIdAndReceiverIdOrderByCreatedAtDesc(Long requesterId, Long receiverId);

    List<FriendRequest> findByReceiverIdAndStatus(Long receiverId, FriendStatus status);
}
