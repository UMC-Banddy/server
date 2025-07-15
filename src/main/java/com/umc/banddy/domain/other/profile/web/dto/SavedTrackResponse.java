package com.umc.banddy.domain.other.profile.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SavedTrackResponse {
    private Long trackId;
    private String title;
    private String artist;
    private String imageUrl;
    private String externalUrl;
}
