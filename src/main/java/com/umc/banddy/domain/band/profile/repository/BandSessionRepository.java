package com.umc.banddy.domain.band.profile.repository;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.BandSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BandSessionRepository extends JpaRepository<BandSession, Long> {
    List<BandSession> findByBandId(Long bandId);

    void deleteAllByBand(Band band);

    List<BandSession> findByBandIdAndSessionStatus(Long bandId, String status);
}
