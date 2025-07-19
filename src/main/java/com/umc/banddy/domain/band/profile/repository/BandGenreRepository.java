package com.umc.banddy.domain.band.profile.repository;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.BandGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BandGenreRepository extends JpaRepository<BandGenre, Long> {
    List<BandGenre> findByBandId(Long bandId);

    void deleteAllByBand(Band band);

    void deleteByBandIdAndGenreIdIn(Long id, List<Long> longs);
}
