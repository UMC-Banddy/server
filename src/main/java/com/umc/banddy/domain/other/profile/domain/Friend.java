package com.umc.banddy.domain.other.profile.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Friend {
    @Id
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "friendship_id")
    private Long friendshipId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

