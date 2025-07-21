package com.umc.banddy.domain.friend.repository;

import com.umc.banddy.domain.friend.domain.Friend;
import com.umc.banddy.domain.friend.domain.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    Optional<Friend> findById(Long id);

    @Query("SELECT f FROM Friend f WHERE (f.memberId = :a AND f.friendshipId = :b) OR (f.memberId = :b AND f.friendshipId = :a)")
    List<Friend> findAllBetween(@Param("a") Long a, @Param("b") Long b);
}
