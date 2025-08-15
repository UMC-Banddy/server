package com.umc.banddy.domain.mypage.notification.repository;

import com.umc.banddy.domain.mypage.notification.enums.ReadStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.umc.banddy.domain.mypage.notification.domain.mapping.ChatNotification;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatNotificationRepository extends JpaRepository<ChatNotification, Long> {

    @EntityGraph (attributePaths = {"notification", "notification.sender"})
    List<ChatNotification> findByNotification_Receiver_Id(Long memberId);

    boolean existsByNotificationSenderIdAndNotificationReceiverIdAndNotificationIsRead(Long senderId, Long receiverId, ReadStatus isRead);

    Optional<ChatNotification> findByNotificationSenderIdAndNotificationReceiverId(Long senderId, Long receiverId) ;
}

