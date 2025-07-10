package com.umc.banddy.domain.music.search.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackInfo {
    private String spotifyId;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private String imageUrl;
}

