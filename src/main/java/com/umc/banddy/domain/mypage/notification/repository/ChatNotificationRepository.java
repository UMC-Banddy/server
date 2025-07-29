package com.umc.banddy.domain.mypage.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.umc.banddy.domain.mypage.notification.domain.mapping.ChatNotification;
import java.util.List;

public interface ChatNotificationRepository extends JpaRepository<ChatNotification, Long> {
    List<ChatNotification> findByReceiverId(Long memberId);
}

