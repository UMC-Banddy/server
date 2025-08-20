package com.umc.banddy.domain.member.domain;

import com.umc.banddy.domain.member.domain.mapping.MemberGenre;
import com.umc.banddy.domain.member.domain.mapping.MemberSession;
import com.umc.banddy.domain.member.enums.Gender;
import com.umc.banddy.domain.member.enums.Status;
import com.umc.banddy.domain.member.enums.Role;
import com.umc.banddy.domain.member.listener.MemberEntityListener;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.global.entity.BaseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(MemberEntityListener.class)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false)
    private Integer age;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 20)
    private String region;  // 시

    @Column(length = 20, nullable = true)
    private String district; // 구

    @Column(length = 500)
    private String refreshToken;

    @Column(nullable = true)
    private String profileImageUrl;

    @Column(nullable = true)
    private String bio;

    @Column(nullable = true)
    private String mediaUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    private LocalDate inactiveDate;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberGenre> memberGenres = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberArtist> memberArtists = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberSession> memberSessions = new ArrayList<>();

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateProfile(String profileImageUrl, String bio, String mediaUrl) {
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
        if (bio != null) this.bio = bio;
        if (mediaUrl != null) this.mediaUrl = mediaUrl;
    }

    public void deactivate() {
        this.status = Status.INACTIVE;
        this.inactiveDate = LocalDate.now();
    }
}