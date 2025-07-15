package com.umc.banddy.domain.mypage.profile.web.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyProfileUpdateRequest {

    private String nickname;
    private Integer age;
    private String gender; // "FEMALE", "MALE"

    private String region;
    private String district;

    private String bio;

    private AvailableSessions availableSessions;

    private List<String> preferredParts;
    private List<String> genres;
    private List<String> artists;
    private List<String> keywords;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableSessions {
        private String vocal;     // BEGINNER, INTERMEDIATE, ADVANCED
        private String instrument;
    }
}

