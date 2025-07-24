package com.umc.banddy.domain.band.profile.repository;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.BandSns;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BandSnsRepository extends JpaRepository<BandSns, Long> {
    List<BandSns> findByBandId(Long bandId);

    void deleteAllByBand(Band band);

    void deleteByBandIdAndPlatformIn(Long id, List<String> strings);
}

