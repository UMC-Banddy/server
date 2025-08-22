package com.umc.banddy.domain.member.domain;

import com.umc.banddy.domain.member.enums.SessionType;
import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Session extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String icon;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 40, unique = true)
    private SessionType sessionType;

}

