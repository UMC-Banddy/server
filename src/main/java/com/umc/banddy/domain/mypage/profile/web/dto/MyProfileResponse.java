package com.umc.banddy.domain.mypage.profile.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyProfileResponse {

    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private String introduction;
    private List<String> hashtags;
    private List<SavedTrack> savedTracks;
    private boolean soundOn;

    @Getter
    @AllArgsConstructor
    public static class SavedTrack {
        private String title;
        private String imageUrl;
    }
}
