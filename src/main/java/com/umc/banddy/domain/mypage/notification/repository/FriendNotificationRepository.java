package com.umc.banddy.domain.mypage.notification.repository;

import com.umc.banddy.domain.mypage.notification.domain.mapping.FriendNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendNotificationRepository extends JpaRepository<FriendNotification, Long> {
    List<FriendNotification> findByNotificationReceiverId(Long memberId);

    void deleteByFriendRequestIdAndType(Long friendRequestId, String type); // 수락 or 거절 시 삭제를 위해 추가
}

