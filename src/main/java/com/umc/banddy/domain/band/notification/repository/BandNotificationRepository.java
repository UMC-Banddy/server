package com.umc.banddy.domain.band.notification.repository;

import com.umc.banddy.domain.band.notification.domain.mapping.BandNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BandNotificationRepository extends JpaRepository<BandNotification, Long> {
    List<BandNotification> findByReceiverId(Long memberId);
}
