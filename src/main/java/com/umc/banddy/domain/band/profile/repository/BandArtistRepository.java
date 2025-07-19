package com.umc.banddy.domain.band.profile.repository;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.BandArtist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BandArtistRepository extends JpaRepository<BandArtist, Long> {
    List<BandArtist> findByBandId(Long bandId);

    void deleteAllByBand(Band band);

    void deleteByBandIdAndArtistIdIn(Long id, List<Long> longs);
}
