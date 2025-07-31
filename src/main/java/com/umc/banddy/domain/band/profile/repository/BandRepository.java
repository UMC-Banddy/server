package com.umc.banddy.domain.band.profile.repository;

import com.umc.banddy.domain.band.profile.domain.Band;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BandRepository extends JpaRepository<Band, Long> {


    List<Band> findByManagerId(Long managerId);

    @Query("""
    select b from Band b
    join fetch b.bandSessions bs
    join fetch bs.session s
    join fetch b.manager
    where b.id = :bandId
""")
    Optional<Band> findWithSessionsAndManager(@Param("bandId") Long bandId);

}

