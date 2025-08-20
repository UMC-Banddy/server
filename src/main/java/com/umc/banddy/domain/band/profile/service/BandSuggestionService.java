package com.umc.banddy.domain.band.profile.service;

import com.umc.banddy.domain.band.profile.converter.BandProfileConverter;
import com.umc.banddy.domain.band.profile.repository.BandArtistRepository;
import com.umc.banddy.domain.band.profile.web.dto.BandSuggestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BandSuggestionService {

    private final BandArtistRepository bandArtistRepository;

    public BandSuggestionResponse getSuggestion(Long bandId) {
        if (bandId == null) {
            return BandProfileConverter.toStaticSuggestion();
        }

        var artists = bandArtistRepository.findByBandId(bandId);
        if (artists == null || artists.isEmpty()) {
            return BandProfileConverter.toStaticSuggestion();
        }

        return BandProfileConverter.toSuggestionResponse(artists);
    }

    public BandSuggestionResponse getStaticSuggestion() {
        return BandProfileConverter.toStaticSuggestion();
    }
}

