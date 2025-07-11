package com.umc.banddy.domain.music.artist.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistToggleResponseDto {
    private Long artistId;
    private String spotifyId;
    private boolean isSaved;
}

