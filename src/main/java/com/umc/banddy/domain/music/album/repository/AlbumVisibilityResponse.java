package com.umc.banddy.domain.music.album.repository;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlbumVisibilityResponse {
    private Long memberId;
    private Boolean isPrivate;
}

