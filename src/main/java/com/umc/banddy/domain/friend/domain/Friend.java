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

    // 사용자 ID
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 친구가 된 상대방 ID
    @Column(name = "friendship_id", nullable = false)
    private Long friendshipId;

    public boolean isBetween(Long member1, Long member2) {
        return (memberId.equals(member1) && friendshipId.equals(member2)) ||
                (memberId.equals(member2) && friendshipId.equals(member1));
    }
}