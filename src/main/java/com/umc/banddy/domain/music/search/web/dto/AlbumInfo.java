package com.umc.banddy.domain.music.search.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlbumInfo {
    private String spotifyId;
    private String name;
    private String artists;
    private String releaseDate;
    private String imageUrl;
}
