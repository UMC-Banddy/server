package com.umc.banddy.domain.other.profile.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OtherProfileResponse {
    private Long memberId;
    private String nickname;
    private String bio;
    private String profileImageUrl;
    private int age;
    private String gender;
    private String region;
    private String district;
    private List<String> tags;
    private List<Session> sessions;
    private List<Artist> favoriteArtists;
    private List<String> traits;
    private String youtubeUrl;
    private String instagramUrl;
    private boolean isFriend;
    private boolean isBlocked;
    private boolean canRequestChat;
    private List<String> genres;

    @Getter
    @AllArgsConstructor
    public static class Session {
        private String name;
        private String icon;
    }

    @Getter
    @AllArgsConstructor
    public static class Artist {
        private String name;
        private String imageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class Album {
        private Long albumId;
        private String title;
        private String artist;
        private String coverImageUrl;
    }
}
