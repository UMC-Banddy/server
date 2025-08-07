package com.umc.banddy.domain.band.profile.service;

import com.umc.banddy.domain.band.profile.converter.BandProfileConverter;
import com.umc.banddy.domain.band.profile.domain.mapping.BandSns;
import com.umc.banddy.domain.band.profile.repository.*;
import com.umc.banddy.domain.band.profile.web.dto.BandDetailResponse;
import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.BandSession;
import com.umc.banddy.domain.band.profile.domain.mapping.BandTag;
import com.umc.banddy.domain.band.profile.domain.mapping.BandTrack;
import com.umc.banddy.domain.band.bookmark.repository.BandBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BandDetailService {

    private final BandRepository bandRepository;
    private final BandSnsRepository bandSnsRepository;

    public BandDetailResponse getBandDetail(Long bandId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 밴드를 찾을 수 없습니다."));

        List<BandSns> snsList = bandSnsRepository.findByBandId(bandId);

        return BandProfileConverter.toDetailResponse(band, snsList);
    }
}

