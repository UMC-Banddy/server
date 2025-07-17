package com.umc.banddy.domain.mypage.similarartist.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SimilarArtistResponse {
    private Long artistId;
    private String name;
    private String imageUrl;
}

