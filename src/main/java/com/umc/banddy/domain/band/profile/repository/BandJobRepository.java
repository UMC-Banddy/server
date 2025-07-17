package com.umc.banddy.domain.band.profile.repository;

import com.umc.banddy.domain.band.profile.domain.mapping.BandJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BandJobRepository extends JpaRepository<BandJob, Long> {
    List<BandJob> findByBandId(Long bandId);
}

