package com.umc.banddy.domain.band.profile.service;

import com.umc.banddy.domain.band.profile.converter.BandProfileConverter;
import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.BandArtist;
import com.umc.banddy.domain.band.profile.domain.mapping.BandSns;
import com.umc.banddy.domain.band.profile.domain.mapping.BandTrack;
import com.umc.banddy.domain.band.profile.repository.*;
import com.umc.banddy.domain.band.profile.web.dto.BandProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BandProfileService {

    private final BandRepository bandRepository;
    private final BandTrackRepository bandTrackRepository;
    private final BandArtistRepository bandArtistRepository;
    private final BandSnsRepository bandSnsRepository;

    public BandProfileResponse getBandProfile(Long bandId, Long currentMemberId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new IllegalArgumentException("해당 밴드가 존재하지 않습니다."));

        List<BandTrack> goalTracks = bandTrackRepository.findByBandId(bandId);
        List<BandArtist> preferredArtists = bandArtistRepository.findByBandId(bandId);
        List<BandSns> snsLinks = bandSnsRepository.findByBandId(bandId);

        return BandProfileConverter.toProfileResponse(band, goalTracks, preferredArtists, snsLinks);
    }
}
