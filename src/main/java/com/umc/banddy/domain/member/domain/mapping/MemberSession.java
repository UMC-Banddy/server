package com.umc.banddy.domain.member.domain.mapping;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.domain.Session;
import com.umc.banddy.domain.member.enums.Level;
import com.umc.banddy.domain.member.enums.SessionType;
import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // nullable = true (생략 시 기본 true)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionType sessionType;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Level level;
}
