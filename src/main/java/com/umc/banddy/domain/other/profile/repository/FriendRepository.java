package com.umc.banddy.domain.other.profile.repository;

import com.umc.banddy.domain.other.profile.domain.Friend;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByMemberIdAndFriendshipId(Long memberId, Long friendId);
}
