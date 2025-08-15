package com.umc.banddy.domain.mypage.profile.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MyProfileResponse {
    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private String bio;
    private Integer age;
    private String gender;
    private String region;
    private List<SessionInfo> sessions;
    private List<String> interestedGenres;
    private List<String> tags;
    private List<SavedTrack> savedTracks;

    @Getter
    @AllArgsConstructor
    public static class SessionInfo {
        private String sessionType;
        private String level;
    }

    @Getter
    @AllArgsConstructor
    public static class SavedTrack {
        private String title;
        private String imageUrl;
        private String externalUrl;
    }
}
