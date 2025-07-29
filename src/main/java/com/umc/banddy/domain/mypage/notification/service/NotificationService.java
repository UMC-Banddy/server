package com.umc.banddy.domain.mypage.notification.service;

import com.umc.banddy.domain.band.notification.domain.mapping.BandNotification;
import com.umc.banddy.domain.band.notification.repository.BandNotificationRepository;
import com.umc.banddy.domain.mypage.notification.converter.NotificationConverter;
//import com.umc.banddy.domain.mypage.notification.domain.mapping.ChatNotification;
import com.umc.banddy.domain.mypage.notification.domain.mapping.ChatNotification;
import com.umc.banddy.domain.mypage.notification.domain.mapping.FriendNotification;
//import com.umc.banddy.domain.mypage.notification.repository.ChatNotificationRepository;
import com.umc.banddy.domain.mypage.notification.repository.ChatNotificationRepository;
import com.umc.banddy.domain.mypage.notification.repository.FriendNotificationRepository;
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
        List<ChatNotification> chats = chatNotificationRepository.findByReceiverId(memberId);
        List<FriendNotification> friends = friendNotificationRepository.findByReceiverId(memberId);
        List<BandNotification> bands = bandNotificationRepository.findByReceiverId(memberId); // ✅ 추가

        return NotificationConverter.mergeAndSort(chats, friends, bands);
    }
}

