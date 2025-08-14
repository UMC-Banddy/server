package com.umc.banddy.domain.mypage.notification.repository;

import com.umc.banddy.domain.mypage.notification.domain.mapping.FriendNotification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendNotificationRepository extends JpaRepository<FriendNotification, Long> {

    @EntityGraph (attributePaths = {"notification", "friendRequest"})
    List<FriendNotification> findByNotification_Receiver_Id(Long memberId);

    void deleteByFriendRequestIdAndType(Long friendRequestId, String type); // 수락 or 거절 시 삭제를 위해 추가
}

