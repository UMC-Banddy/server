package com.umc.banddy.domain.mypage.notification.domain.mapping;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.mypage.notification.domain.Notification;
import com.umc.banddy.domain.mypage.notification.enums.ReadStatus;
import com.umc.banddy.domain.friend.domain.FriendRequest;
import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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

    private String type; // 일단 보류

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_request_id")
    private FriendRequest friendRequest;

    @Column(length = 50, nullable = true)
    @Size(max = 50, message = "메시지는 최대 50자까지 입력 가능합니다.")
    private String message;

}
