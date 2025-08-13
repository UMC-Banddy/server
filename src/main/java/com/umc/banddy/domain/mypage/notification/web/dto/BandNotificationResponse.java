package com.umc.banddy.domain.mypage.notification.web.dto;

import com.umc.banddy.domain.mypage.notification.enums.NotificationType;
import com.umc.banddy.domain.mypage.notification.enums.ReadStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BandNotificationResponse(
        Long notificationId,
        String title,
        NotificationType type,
        String imageUrl,
        ReadStatus isRead,
        LocalDateTime createdAt,
        Long senderId
) implements NotificationResponse {
    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}