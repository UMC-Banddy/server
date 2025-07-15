package com.umc.banddy.domain.other.profile.domain;

import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Friend extends BaseEntity {
    @Id
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "friendship_id")
    private Long friendshipId;
}

