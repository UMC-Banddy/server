package com.umc.banddy.domain.mypage.notification.service;

import com.umc.banddy.domain.band.notification.domain.mapping.BandNotification;
import com.umc.banddy.domain.mypage.notification.domain.mapping.ChatNotification;
import com.umc.banddy.domain.mypage.notification.domain.mapping.FriendNotification;
import com.umc.banddy.domain.band.notification.repository.BandNotificationRepository;
import com.umc.banddy.domain.mypage.notification.repository.ChatNotificationRepository;
import com.umc.banddy.domain.mypage.notification.repository.FriendNotificationRepository;
import com.umc.banddy.domain.mypage.notification.converter.NotificationConverter;
import com.umc.banddy.domain.mypage.notification.web.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ChatNotificationRepository chatNotificationRepository;
    private final FriendNotificationRepository friendNotificationRepository;
    private final BandNotificationRepository bandNotificationRepository;

    public List<NotificationResponse> getAllNotifications(Long memberId) {
        List<ChatNotification> chats = chatNotificationRepository.findByNotificationReceiverId(memberId);
        List<FriendNotification> friends = friendNotificationRepository.findByNotificationReceiverId(memberId);
        List<BandNotification> bands = bandNotificationRepository.findByNotificationReceiverId(memberId);

        for (FriendNotification f : friends) {
            System.out.println("💡 알림 ID: " + f.getNotification().getId());
            System.out.println("💡 요청 ID: " + (f.getFriendRequest() != null ? f.getFriendRequest().getId() : "null"));
            System.out.println("💡 요청 상태: " + (f.getFriendRequest() != null ? f.getFriendRequest().getStatus() : "null"));
        }

        return NotificationConverter.mergeAndSort(chats, friends, bands);
    }
}
