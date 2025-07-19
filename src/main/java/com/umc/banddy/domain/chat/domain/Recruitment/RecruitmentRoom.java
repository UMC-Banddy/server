package com.umc.banddy.domain.chat.domain.Recruitment;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Gender;
import com.umc.banddy.domain.member.enums.Status;
import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@Builder
@Setter
@NoArgsConstructor(access = PROTECTED)
public class RecruitmentRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bandImageUrl;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime recruitmentEndDate;

    @Column(nullable = false)
    private String recruitmentIntro;

    @Column(nullable = false)
    private int ageStart;

    @Column(nullable = false)
    private int ageEnd;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 20)
    private String region;  // 시

    @Column(length = 20)
    private String district; // 구

    @Column(nullable = false)
    private Status status; // 모집 상태

    @Column(nullable = false)
    private String avgAge; // 평균 나이

    @Column(nullable = false)
    private Long maleCount;

    @Column(nullable = false)
    private Long femaleCount;

    @Column(nullable = false)
    private boolean isDeleted = false; // 삭제 여부

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "manager_id", unique = true)
    private Member manager;

    @OneToMany(mappedBy = "recruitmentRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentSession> recruitmentSession = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentGenre> recruitmentGenre = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentChatRoom> recruitmentChatRoom= new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentTrack> recruitmentTrack = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentJob> recruitmentJob = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentSNS> recruitmentSNS = new ArrayList<>();
}
