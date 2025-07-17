package com.umc.banddy.domain.band.profile.repository;

import com.umc.banddy.domain.band.profile.domain.mapping.BandTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BandTrackRepository extends JpaRepository<BandTrack, Long> {
    List<BandTrack> findByBandId(Long bandId);
}
