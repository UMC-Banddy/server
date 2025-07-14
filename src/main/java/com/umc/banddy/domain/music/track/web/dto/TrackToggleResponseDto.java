package com.umc.banddy.domain.music.track.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackToggleResponseDto {
    private Long trackId;
    private String spotifyId;
    private boolean isSaved;
}
