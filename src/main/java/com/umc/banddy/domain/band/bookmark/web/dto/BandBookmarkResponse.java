package com.umc.banddy.domain.band.bookmark.web.dto;

import com.umc.banddy.domain.band.profile.enums.BandStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BandBookmarkResponse {
    private Long bandId;
    private String name;
    private String imageUrl;
    private BandStatus status;
    private boolean isSoundOn;
    private String memberSummary;
    private int memberCount;
}
