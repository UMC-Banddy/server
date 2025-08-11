package com.umc.banddy.domain.mypage.notification.repository;

import com.umc.banddy.domain.mypage.notification.enums.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.umc.banddy.domain.mypage.notification.domain.mapping.ChatNotification;
import java.util.List;

public interface ChatNotificationRepository extends JpaRepository<ChatNotification, Long> {
    List<ChatNotification> findByNotificationReceiverId(Long memberId);
    boolean existsBySenderIdAndReceiverIdAndIsRead(Long senderId, Long receiverId, ReadStatus isRead);

}

