package com.umc.banddy.domain.band.preference.converter;

import com.umc.banddy.domain.band.preference.web.dto.PreferenceTrackResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PreferenceTrackConverter {

    public PreferenceTrackResponse toResponse(List<String> tags, List<String> artists, List<String> tracks) {
        return PreferenceTrackResponse.builder()
                .preferredTags(tags)
                .preferredArtists(artists)
                .preferredTracks(tracks)
                .build();
    }
}

