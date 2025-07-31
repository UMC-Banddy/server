package com.umc.banddy.domain.mypage.notification.domain.mapping;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.mypage.notification.domain.Notification;
import com.umc.banddy.domain.mypage.notification.enums.ReadStatus;
import com.umc.banddy.domain.friend.domain.FriendRequest;
import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FriendNotification {

    @Id
    private Long notificationId;  // Notification과 공유할 ID

    @OneToOne
    @MapsId
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_read", nullable = false)
    private ReadStatus isRead;

    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_request_id")
    private FriendRequest friendRequest;

    public void markAsRead() {
        this.isRead = ReadStatus.READ;
    }
}
