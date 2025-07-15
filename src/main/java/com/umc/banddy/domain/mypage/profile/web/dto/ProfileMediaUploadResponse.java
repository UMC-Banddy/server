package com.umc.banddy.domain.mypage.profile.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProfileMediaUploadResponse {
    private String url;
}

