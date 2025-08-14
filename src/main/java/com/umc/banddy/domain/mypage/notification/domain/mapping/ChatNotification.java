package com.umc.banddy.domain.mypage.notification.domain.mapping;

import com.umc.banddy.domain.mypage.notification.domain.Notification;
import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatNotification extends BaseEntity {

    @Id
    private Long notificationId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @Column(length = 50, nullable = true)
    @Size(max = 50, message = "메시지는 최대 50자까지 입력 가능합니다.")
    private String message;

}
