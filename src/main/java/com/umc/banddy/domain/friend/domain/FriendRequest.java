package com.umc.banddy.domain.friend.domain;

import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "friend_request")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FriendRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;  // REQUESTED, REJECTED

    public boolean isBetween(Long memberA, Long memberB) {
        return (requesterId.equals(memberA) && receiverId.equals(memberB)) ||
                (requesterId.equals(memberB) && receiverId.equals(memberA));
    }

    public boolean isRequester(Long memberId) {
        return requesterId.equals(memberId);
    }

    public boolean isReceiver(Long memberId) {
        return receiverId.equals(memberId);
    }
}
