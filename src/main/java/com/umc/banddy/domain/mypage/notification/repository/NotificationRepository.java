package com.umc.banddy.domain.mypage.notification.repository;

import com.umc.banddy.domain.mypage.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
