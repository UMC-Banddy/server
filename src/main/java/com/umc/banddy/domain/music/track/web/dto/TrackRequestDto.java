package com.umc.banddy.domain.music.track.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class TrackRequestDto {

    @Getter
    public static class TrackSaveDto {
        @NotBlank
        String spotifyId;
    }
}
