package com.umc.banddy.domain.band.profile.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class BandDetailResponse {

    private Long bandId;
    private String name;
    private String imageUrl;
    private Boolean isBookmarked;
    private List<String> recruitingSessions;
    private String description;
    private List<String> tags;

    private List<TrackDto> tracks;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TrackDto {
        private Long trackId;
        private String title;
        private String artist;
        private String imageUrl;
    }
}

