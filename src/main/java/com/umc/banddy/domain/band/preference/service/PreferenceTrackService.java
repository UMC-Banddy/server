package com.umc.banddy.domain.band.preference.service;

import com.umc.banddy.domain.band.preference.converter.PreferenceTrackConverter;
import com.umc.banddy.domain.band.preference.repository.MemberPreferenceRepository;
import com.umc.banddy.domain.band.preference.web.dto.PreferenceTrackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceTrackService {

    private final MemberPreferenceRepository memberPreferenceRepository;
    private final PreferenceTrackConverter converter;

    public PreferenceTrackResponse getPreferences(Long memberId) {
        var tags = memberPreferenceRepository.findPreferredTags(memberId);
        var artists = memberPreferenceRepository.findPreferredArtists(memberId);
        var tracks = memberPreferenceRepository.findPreferredTracks(memberId);

        return converter.toResponse(tags, artists, tracks);
    }
}

