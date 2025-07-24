package com.umc.banddy.domain.band.profile.service;

import com.umc.banddy.domain.band.profile.converter.BandProfileConverter;
import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.*;
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
    private final BandSessionRepository bandSessionRepository;
    private final BandJobRepository bandJobRepository;

    public BandProfileResponse getBandProfile(Long bandId, Long currentMemberId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new IllegalArgumentException("해당 밴드가 존재하지 않습니다."));

        List<BandTrack> goalTracks = bandTrackRepository.findByBandId(bandId);
        List<BandArtist> preferredArtists = bandArtistRepository.findByBandId(bandId);
        List<BandSns> snsLinks = bandSnsRepository.findByBandId(bandId);
        List<BandSession> sessions = bandSessionRepository.findByBandIdAndSessionStatus(bandId, "PARTICIPATING");// 나중에 전체적으로 enum을 바꾸는게 좋을거 같긴한데...
        List<BandJob> jobs = bandJobRepository.findJobsByBandId(bandId); // sns랑 job은 모집에 List랑 Map으로 수정하는 것도 괜찮아보임

        return BandProfileConverter.toProfileResponse(band, goalTracks, preferredArtists, snsLinks, sessions, jobs);
    }
}
