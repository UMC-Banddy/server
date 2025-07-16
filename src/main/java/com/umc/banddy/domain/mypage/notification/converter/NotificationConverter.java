package com.umc.banddy.domain.mypage.notification.converter;

import com.umc.banddy.domain.mypage.notification.domain.ChatNotification;
import com.umc.banddy.domain.mypage.notification.domain.FriendNotification;
import com.umc.banddy.domain.mypage.notification.web.dto.NotificationResponse;
import com.umc.banddy.domain.mypage.notification.enums.NotificationType;

import java.util.ArrayList;
import java.util.List;

public class NotificationConverter {

    public static NotificationResponse fromChat(ChatNotification n) {
        String profileImage = n.getReceiver().getProfileImageUrl();  // 예시 getter

        return NotificationResponse.builder()
                .notificationId(n.getId())
                .title("새 메시지가 도착했습니다.")
                .type(NotificationType.CHAT)
                .imageUrl(profileImage)
                .build();
    }

    public static NotificationResponse fromFriend(FriendNotification n) {
        String profileImage = n.getSender().getProfileImageUrl();

        return NotificationResponse.builder()
                .notificationId(n.getId())
                .title(n.getSender().getNickname() + "님이 친구 요청을 보냈습니다.")
                .type(NotificationType.FRIEND)
                .imageUrl(profileImage)
                .build();
    }

    // ⚠️ 밴드 알림 예시는 별도 BandNotification 도메인이 있다고 가정
    public static NotificationResponse fromBand(Long id, String title, String coverImage) {
        return NotificationResponse.builder()
                .notificationId(id)
                .title(title)
                .type(NotificationType.BAND)
                .imageUrl(coverImage)
                .build();
    }

    public static List<NotificationResponse> mergeAndSort(
            List<ChatNotification> chat,
            List<FriendNotification> friend
            // + List<BandNotification> band (예시일 뿐)
    ) {
        List<NotificationResponse> result = new ArrayList<>();
        chat.forEach(c -> result.add(fromChat(c)));
        friend.forEach(f -> result.add(fromFriend(f)));
        // band.forEach(b -> result.add(fromBand(...)))

        return result;
    }
}
