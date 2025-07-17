package com.umc.banddy.domain.band.profile.service;

import com.umc.banddy.domain.band.profile.converter.BandProfileConverter;
import com.umc.banddy.domain.band.profile.web.dto.BandDetailResponse;
import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.BandSession;
import com.umc.banddy.domain.band.profile.domain.mapping.BandTag;
import com.umc.banddy.domain.band.profile.domain.mapping.BandTrack;
import com.umc.banddy.domain.band.profile.repository.BandRepository;
import com.umc.banddy.domain.band.profile.repository.BandSessionRepository;
import com.umc.banddy.domain.band.profile.repository.BandTagRepository;
import com.umc.banddy.domain.band.profile.repository.BandTrackRepository;
import com.umc.banddy.domain.band.profile.repository.BandBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BandDetailService {

    private final BandRepository bandRepository;
    private final BandBookmarkRepository bandBookmarkRepository;
    private final BandSessionRepository bandSessionRepository;
    private final BandTagRepository bandTagRepository;
    private final BandTrackRepository bandTrackRepository;

    public BandDetailResponse getBandDetail(Long loginMemberId, Long bandId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new IllegalArgumentException("해당 밴드가 존재하지 않습니다."));

        boolean isBookmarked = bandBookmarkRepository.existsByMemberIdAndBandId(loginMemberId, bandId);
        List<BandSession> sessions = bandSessionRepository.findByBandId(bandId);
        List<BandTag> tags = bandTagRepository.findByBandId(bandId);
        List<BandTrack> tracks = bandTrackRepository.findByBandId(bandId);

        return BandProfileConverter.toDetailResponse(band, isBookmarked, sessions, tags, tracks);
    }
}

