package com.umc.banddy.domain.mypage.profile.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyProfileUpdateRequest {
    private String nickname;
    private Integer age;
    private String gender;
    private String region;
    private String district;
    private String bio;
}
