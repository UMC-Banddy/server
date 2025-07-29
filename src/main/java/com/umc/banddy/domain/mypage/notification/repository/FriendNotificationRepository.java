package com.umc.banddy.domain.mypage.notification.repository;

import com.umc.banddy.domain.mypage.notification.domain.mapping.FriendNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendNotificationRepository extends JpaRepository<FriendNotification, Long> {
    List<FriendNotification> findByNotificationReceiverId(Long memberId);
}

