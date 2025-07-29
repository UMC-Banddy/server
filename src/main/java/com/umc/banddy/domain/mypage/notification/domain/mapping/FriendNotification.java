package com.umc.banddy.domain.mypage.notification.domain.mapping;

import com.umc.banddy.domain.friend.domain.FriendRequest;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.mypage.notification.domain.Notification;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FriendNotification {

    @Id
    private Long notificationId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_request_id", nullable = false)
    private FriendRequest friendRequest;

    @Column(nullable = false)
    private String type; // "REQUEST", "ACCEPT" 등
}


