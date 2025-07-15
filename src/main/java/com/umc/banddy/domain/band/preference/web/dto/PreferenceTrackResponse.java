package com.umc.banddy.domain.band.preference.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PreferenceTrackResponse {
    private List<String> preferredTags;
    private List<String> preferredArtists;
    private List<String> preferredTracks;
}
