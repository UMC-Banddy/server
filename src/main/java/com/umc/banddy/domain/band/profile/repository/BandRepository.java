package com.umc.banddy.domain.band.profile.repository;

import com.umc.banddy.domain.band.profile.domain.Band;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BandRepository extends JpaRepository<Band, Long> {
}

