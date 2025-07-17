package com.umc.banddy.domain.mypage.notification.converter;

import com.umc.banddy.domain.mypage.notification.domain.ChatNotification;
import com.umc.banddy.domain.mypage.notification.domain.FriendNotification;
import com.umc.banddy.domain.mypage.notification.web.dto.NotificationResponse;
import com.umc.banddy.domain.mypage.notification.enums.NotificationType;
import com.umc.banddy.domain.band.notification.domain.mapping.BandNotification;

import java.util.ArrayList;
import java.util.List;

public class NotificationConverter {

    public static NotificationResponse fromChat(ChatNotification n) {
        String profileImage = n.getReceiver().getProfileImageUrl();

        return NotificationResponse.builder()
                .notificationId(n.getId())
                .title("새 메시지가 도착했습니다.")
                .type(NotificationType.CHAT)
                .imageUrl(profileImage)
                .createdAt(n.getCreatedAt())
                .build();
    }

    public static NotificationResponse fromFriend(FriendNotification n) {
        String profileImage = n.getSender().getProfileImageUrl();

        return NotificationResponse.builder()
                .notificationId(n.getId())
                .title(n.getSender().getNickname() + "님이 친구 요청을 보냈습니다.")
                .type(NotificationType.FRIEND)
                .imageUrl(profileImage)
                .createdAt(n.getCreatedAt())
                .build();
    }

    public static NotificationResponse fromBand(BandNotification n) {
        return NotificationResponse.builder()
                .notificationId(n.getId())
                .title(n.getTitle())
                .type(NotificationType.BAND)
                .imageUrl(n.getBand().getProfileImageUrl())
                .createdAt(n.getCreatedAt())
                .build();
    }

    public static List<NotificationResponse> mergeAndSort(
            List<ChatNotification> chat,
            List<FriendNotification> friend,
            List<BandNotification> band
    ) {
        List<NotificationResponse> result = new ArrayList<>();
        chat.forEach(c -> result.add(fromChat(c)));
        friend.forEach(f -> result.add(fromFriend(f)));
        band.forEach(b -> result.add(fromBand(b)));

        result.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));
        return result;
    }
}
