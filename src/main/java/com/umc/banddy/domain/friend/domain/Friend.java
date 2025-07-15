package com.umc.banddy.domain.friend.domain;

import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "friend")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "friendship_id", nullable = false)
    private Long friendshipId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;
}