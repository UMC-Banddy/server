package com.umc.banddy.domain.mypage.notification.service;

import com.umc.banddy.domain.mypage.notification.domain.mapping.BandNotification;
import com.umc.banddy.domain.mypage.notification.domain.mapping.ChatNotification;
import com.umc.banddy.domain.mypage.notification.domain.mapping.FriendNotification;
import com.umc.banddy.domain.mypage.notification.repository.BandNotificationRepository;
import com.umc.banddy.domain.mypage.notification.repository.ChatNotificationRepository;
import com.umc.banddy.domain.mypage.notification.repository.FriendNotificationRepository;
import com.umc.banddy.domain.mypage.notification.converter.NotificationConverter;
import com.umc.banddy.domain.mypage.notification.enums.NotificationType;
import com.umc.banddy.domain.mypage.notification.web.dto.NotificationResponse;
import jakarta.transaction.Transactional;
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
        List<ChatNotification> chats = chatNotificationRepository.findByNotification_Receiver_Id(memberId);
        List<FriendNotification> friends = friendNotificationRepository.findByNotification_Receiver_Id(memberId);
        List<BandNotification> bands = bandNotificationRepository.findByNotification_Receiver_Id(memberId);

        return NotificationConverter.mergeAndSort(chats, friends, bands);
    }

    @Transactional
    public void markNotificationAsRead(NotificationType type, Long id) {
        switch (type) {
            case CHAT -> {
                ChatNotification n = chatNotificationRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("채팅 알림이 존재하지 않습니다."));
                n.getNotification().markAsRead();
            }
            case FRIEND -> {
                FriendNotification n = friendNotificationRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("친구 알림이 존재하지 않습니다."));
                n.getNotification().markAsRead();
            }
            case BAND -> {
                BandNotification n = bandNotificationRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("밴드 알림이 존재하지 않습니다."));
                n.getNotification().markAsRead();
            }
            default -> throw new IllegalArgumentException("알 수 없는 알림 타입입니다.");
        }
    }
}
