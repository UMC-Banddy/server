package com.umc.banddy.domain.member.domain;

import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.umc.banddy.domain.member.enums.Platform;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SnsLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Platform platform;

    @Column(nullable = true)
    private String url;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
