package com.umc.banddy.domain.music.album.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumResponseDto {
    private Long albumId;
    private String spotifyId;
    private String name;
    private String artist;
    private String imageUrl;
    private String externalUrl;
}
