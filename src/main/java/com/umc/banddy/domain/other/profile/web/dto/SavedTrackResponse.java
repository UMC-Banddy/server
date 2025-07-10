package com.umc.banddy.domain.other.profile.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SavedTrackResponse {
    private Long trackId;
    private String title;
    private String artist;
    private String imageUrl;
    private String soundUrl;
}

