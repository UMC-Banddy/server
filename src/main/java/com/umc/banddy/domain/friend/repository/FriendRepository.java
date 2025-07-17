package com.umc.banddy.domain.friend.repository;

import com.umc.banddy.domain.friend.domain.Friend;
import com.umc.banddy.domain.friend.domain.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    Optional<Friend> findByMemberIdAndFriendshipId(Long memberId, Long friendshipId);

    @Query("SELECT f FROM Friend f WHERE (f.memberId = :id OR f.friendshipId = :id) AND f.status = :status")
    List<Friend> findAcceptedFriends(@Param("id") Long memberId, @Param("status") FriendStatus status);

    // 받은 친구 요청 목록 (REQUESTED 상태만)
    List<Friend> findByFriendshipIdAndStatus(Long receiverId, FriendStatus status);
}
