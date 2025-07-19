package com.umc.banddy.domain.band.profile.repository;

import com.umc.banddy.domain.band.profile.domain.mapping.BandTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BandTagRepository extends JpaRepository<BandTag, Long> {

    List<BandTag> findByBandId(Long bandId);
}
