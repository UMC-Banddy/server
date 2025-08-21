package com.umc.banddy.domain.mypage.profile.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyProfileUpdateRequest {

    private String nickname;
    private Integer age;
    private String gender;

    private String region;
    private String district;
    private String bio;

    private String mediaUrl;

    private List<SessionInfo> availableSessions;

    private List<String> genres;
    private List<String> artists;
    private List<String> keywords;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionInfo {
        private String sessionType;
        private String level;
    }
}
