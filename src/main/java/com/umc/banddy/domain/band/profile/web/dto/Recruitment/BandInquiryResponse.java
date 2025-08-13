package com.umc.banddy.domain.band.profile.web.dto.Recruitment;

import com.umc.banddy.domain.band.profile.enums.BandStatus;
import com.umc.banddy.domain.band.profile.enums.Gender;
import com.umc.banddy.domain.member.domain.Genre;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class BandInquiryResponse {

    private BandStatus status;

    private String profileImageUrl;

    private representativeSong representativeSong;

    private String name;

    private LocalDateTime endDate;

    private Boolean autoClose;

    private String description;

    private List<String> sessions;

    private List<String> genres;

    private List<Artist> artists;

    private List<Track> tracks;

    private Integer ageStart;

    private Integer ageEnd;

    private String gender;

    private String region;

    private String averageAge;

    private List<String> jobs;

    private Integer maleCount;

    private Integer femaleCount;

    private List<String> currentSessions;

    private Map<String,String> snsLink;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class representativeSong{
        private String spotifyId;
        private String artist;
        private String trackTitle;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Artist{
        private String spotifyId;
        private String name;
        private String ImageUrl;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Track{
        private String spotifyId;
        private String title;
        private String imageUrl;
    }







}
