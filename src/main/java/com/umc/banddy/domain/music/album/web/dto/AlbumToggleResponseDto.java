package com.umc.banddy.domain.music.album.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumToggleResponseDto {
    private Long albumId;
    private String spotifyId;
    private boolean isSaved;
}
