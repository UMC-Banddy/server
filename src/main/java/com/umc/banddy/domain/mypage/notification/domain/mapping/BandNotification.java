package com.umc.banddy.domain.mypage.notification.domain.mapping;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.chat.domain.enums.PassFail;
import com.umc.banddy.domain.mypage.notification.domain.Notification;
import com.umc.banddy.domain.mypage.notification.enums.ReadStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BandNotification {

    @Id
    private Long notificationId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id", nullable = false)
    private Band band;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PassFail passFail;

}
