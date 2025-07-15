package com.umc.banddy.domain.member.domain;

import com.umc.banddy.domain.member.enums.Gender;
import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

    @Column(length = 20)
    private String district; // 구

    @Column(length = 500)
    private String refreshToken;

    @Column(nullable = true)
    private String profileImageUrl;

    @Column(nullable = true)
    private String introduction;

    @Column(nullable = true)
    private String mediaUrl;

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateProfile(String profileImageUrl, String introduction, String mediaUrl) {
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.mediaUrl = mediaUrl;
    }
}