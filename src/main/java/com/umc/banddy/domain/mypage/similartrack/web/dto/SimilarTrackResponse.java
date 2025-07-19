package com.umc.banddy.domain.mypage.similartrack.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SimilarTrackResponse {
    private Long trackId;
    private String title;
    private String artist;
    private String album;
    private String imageUrl;
}

