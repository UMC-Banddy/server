package com.umc.banddy.domain.mypage.notification.web.dto;

import com.umc.banddy.domain.mypage.notification.enums.NotificationType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        Long notificationId,
        String title,
        NotificationType type,
        String imageUrl,
        LocalDateTime createdAt,
        Long friendRequestId  //친구 요청을 알림을 위해 추가
) {}

