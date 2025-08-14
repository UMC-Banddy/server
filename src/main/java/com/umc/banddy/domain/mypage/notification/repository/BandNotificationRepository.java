package com.umc.banddy.domain.mypage.notification.repository;

import com.umc.banddy.domain.mypage.notification.domain.mapping.BandNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BandNotificationRepository extends JpaRepository<BandNotification, Long> {
    List<BandNotification> findByNotification_Receiver_Id(Long memberId);
}
