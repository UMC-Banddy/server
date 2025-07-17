package com.umc.banddy.domain.band.profile.repository;

import com.umc.banddy.domain.band.profile.domain.mapping.BandBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BandBookmarkRepository extends JpaRepository<BandBookmark, Long> {

    boolean existsByMemberIdAndBandId(Long memberId, Long bandId);
}

