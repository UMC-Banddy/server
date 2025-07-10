package com.umc.banddy.domain.music.track.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TrackResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackResultDto {
        Long trackId;
        String spotifyId;
        String title;
        String artist;
        String album;
        String duration;
        String imageUrl;
        Long memberTrackId;
    }
}
