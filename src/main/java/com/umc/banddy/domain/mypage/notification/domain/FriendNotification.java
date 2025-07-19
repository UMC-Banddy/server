package com.umc.banddy.domain.mypage.notification.domain;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.mypage.notification.domain.mapping.FriendRequest;
import com.umc.banddy.domain.mypage.notification.enums.ReadStatus;
import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FriendNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReadStatus isRead;

    private String type; // e.g., REQUEST, ACCEPT 등

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_request_id")
    private FriendRequest friendRequest;
}

