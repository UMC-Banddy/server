package com.umc.banddy.domain.mypage.notification.service;

import com.umc.banddy.domain.mypage.notification.converter.NotificationConverter;
//import com.umc.banddy.domain.mypage.notification.domain.ChatNotification;
import com.umc.banddy.domain.mypage.notification.domain.ChatNotification;
import com.umc.banddy.domain.mypage.notification.domain.FriendNotification;
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

    public List<NotificationResponse> getAllNotifications(Long memberId) {
        List<ChatNotification> chats = chatNotificationRepository.findByReceiverId(memberId);
        List<FriendNotification> friends = friendNotificationRepository.findByReceiverId(memberId);
        return NotificationConverter.mergeAndSort(chats, friends);
    }
}

