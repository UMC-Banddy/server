package com.umc.banddy.domain.mypage.profile.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MyProfileUpdateRequest {

    private String nickname;
    private Integer age;
    private String gender; // FEMALE, MALE 등

    private Region region;
    private AvailableSessions availableSessions;

    private List<String> preferredParts;
    private List<String> genres;
    private List<String> artists;
    private List<String> keywords;

    private String bio;

    @Getter
    @Setter
    public static class Region {
        private String city;
        private String district;
    }

    @Getter
    @Setter
    public static class AvailableSessions {
        private String vocal;     // BEGINNER, INTERMEDIATE, ADVANCED
        private String instrument;
    }
}
