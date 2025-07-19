package com.umc.banddy.domain.band.profile.domain;

import com.umc.banddy.domain.member.domain.Session;
import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Band extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이미지, 기본 정보
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "representative_song")
    private String representativeSong;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 모집 관련
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "auto_Close")
    private Boolean autoClose; // 모집 종료일에 자동 종료 여부

    @Column(name = "age_start")
    private Integer ageStart;

    @Column(name = "age_end")
    private Integer ageEnd;

    private String job;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "band_session", joinColumns = @JoinColumn(name = "band_id"))
    private List<Session> sessions;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private com.umc.banddy.domain.band.profile.enums.Gender gender;

    @Column(name = "region", length = 20)
    private String region;

    @Column(name = "district", length = 30)
    private String district;

    // 통계 및 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private com.umc.banddy.domain.band.profile.enums.BandStatus status;

    @Column(name = "average_age", length = 20)
    private String averageAge;

    @Column(name = "male_count")
    private Integer maleCount;

    @Column(name = "female_count")
    private Integer femaleCount;

}

