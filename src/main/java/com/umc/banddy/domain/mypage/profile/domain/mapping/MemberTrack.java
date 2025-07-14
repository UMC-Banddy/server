package com.umc.banddy.domain.mypage.profile.domain.mapping;

import com.umc.banddy.domain.mypage.profile.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_track")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberTrack {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private Track track;

    @Column(name = "sound_on")
    private String soundOn;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
