package com.umc.banddy.domain.music.search.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistInfo {
    private String spotifyId;
    private String name;
    private String genres;
    private String imageUrl;
}
