package com.umc.banddy.domain.mypage.notification.web.dto;

import com.umc.banddy.domain.mypage.notification.enums.NotificationType;
import lombok.Builder;

@Builder
public record NotificationResponse(
        Long notificationId,
        String title,
        NotificationType type,
        String imageUrl
) {}

