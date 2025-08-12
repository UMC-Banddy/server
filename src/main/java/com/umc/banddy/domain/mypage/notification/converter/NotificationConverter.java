package com.umc.banddy.domain.mypage.notification.converter;

import com.umc.banddy.domain.mypage.notification.domain.mapping.ChatNotification;
import com.umc.banddy.domain.mypage.notification.domain.mapping.FriendNotification;
import com.umc.banddy.domain.band.notification.domain.mapping.BandNotification;
import com.umc.banddy.domain.mypage.notification.web.dto.NotificationResponse;
import com.umc.banddy.domain.mypage.notification.enums.NotificationType;

import java.util.ArrayList;
import java.util.List;

public class NotificationConverter {

    public static NotificationResponse fromChat(ChatNotification n) {
        var sender = n.getNotification().getSender();
        return NotificationResponse.builder()
                .notificationId(n.getNotification().getId())
                .title(sender.getNickname() + "님이 채팅을 요청했습니다")
                .type(NotificationType.CHAT)
                .imageUrl(sender.getProfileImageUrl())
                .createdAt(n.getNotification().getCreatedAt())
                .senderId(sender.getId())
                .build();
    }

    public static NotificationResponse fromFriend(FriendNotification n) {
        var sender = n.getSender();
        return NotificationResponse.builder()
                .notificationId(n.getNotification().getId())
                .title(sender.getNickname() + "님이 친구 요청을 보냈습니다.")
                .type(NotificationType.FRIEND)
                .imageUrl(sender.getProfileImageUrl())
                .createdAt(n.getNotification().getCreatedAt())
                .senderId(sender.getId())
                .friendRequestId(n.getFriendRequest().getId())
                .build();
    }

    public static NotificationResponse fromBand(BandNotification n) {
        var sender = n.getNotification().getSender();
        return NotificationResponse.builder()
                .notificationId(n.getNotification().getId())
                .title(n.getTitle())
                .type(NotificationType.BAND)
                .imageUrl(n.getBand().getProfileImageUrl())
                .createdAt(n.getNotification().getCreatedAt())
                .senderId(sender.getId())
                .build();
    }

    //통합
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
