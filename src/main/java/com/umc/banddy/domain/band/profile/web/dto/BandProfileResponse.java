package com.umc.banddy.domain.band.profile.web.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BandProfileResponse {

    private List<TrackDto> goalTracks;
    private List<ArtistDto> preferredArtists;
    private CompositionDto composition;
    private List<SnsDto> sns;
    private List<String> sessions;
    private List<String> jobs;

    @Getter @AllArgsConstructor
    public static class TrackDto {
        private String title;
        private String artist;
        private String imageUrl;
    }

    @Getter @AllArgsConstructor
    public static class ArtistDto {
        private String name;
        private String imageUrl;
    }

    @Getter @Builder
    public static class CompositionDto {
        private String averageAge;
        //private String job;
        private int maleCount;
        private int femaleCount;
    }

    @Getter @AllArgsConstructor
    public static class SnsDto {
        private String platform;
        private String url;
    }
}
