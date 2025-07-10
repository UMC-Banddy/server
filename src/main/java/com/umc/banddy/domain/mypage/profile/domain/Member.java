package com.umc.banddy.domain.mypage.profile.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {
    @Id
    private Long id;

    private String nickname;
    private Integer age;
    private String gender;
    private String region;
    private String district;
    private String bio;

    @Column(name = "profile_image_url")
    private String profileImageUrl;
}
