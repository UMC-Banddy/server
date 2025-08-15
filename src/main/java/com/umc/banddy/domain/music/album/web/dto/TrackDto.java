package com.umc.banddy.domain.music.album.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrackDto {
    private String spotifyId;
    private String name;
    private int trackNumber;
    private int discNumber;
    //private int durationMs;
    private String duration;
    private boolean explicit;
    private String spotifyUrl;
}
