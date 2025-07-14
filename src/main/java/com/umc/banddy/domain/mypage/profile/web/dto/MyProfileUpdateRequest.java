package com.umc.banddy.domain.mypage.profile.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyProfileUpdateRequest {
    private String nickname;
    private Integer age;
    private String gender;
    private String region;
    private String district;
    private String bio;
}

