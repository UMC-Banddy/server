package com.umc.banddy.domain.mypage.notification.web.dto;

import java.time.LocalDateTime;

public sealed interface NotificationResponse
        permits ChatNotificationResponse, FriendNotificationResponse, BandNotificationResponse {
    LocalDateTime getCreatedAt();
}


