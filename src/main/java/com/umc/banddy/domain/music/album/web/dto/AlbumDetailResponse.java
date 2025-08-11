package com.umc.banddy.domain.music.album.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AlbumDetailResponse {
    private Long albumId;
    private String spotifyId;
    private String name;
    private String artist;
    private String imageUrl;
    private String externalUrl;
    private int total; // 전체 트랙 개수
    private List<TrackDto> tracks;
}
