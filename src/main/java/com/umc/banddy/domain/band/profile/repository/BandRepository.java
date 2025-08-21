package com.umc.banddy.domain.band.profile.repository;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.member.domain.Member;
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

    @Query("""
        select b
          from Band b
         where b.manager.id = :managerId
           and b.status <> com.umc.banddy.domain.band.profile.enums.BandStatus.ENDED
        """)
    List<Band> findAllActiveByManagerId(@Param("managerId") Long managerId);

    @Query("""
    SELECT DISTINCT b
    FROM Band b
    LEFT JOIN FETCH b.bandSessions bs
    LEFT JOIN FETCH bs.session s
    LEFT JOIN FETCH b.representativeTrack rt
    WHERE b.manager.id <> :memberId
      AND b.status = com.umc.banddy.domain.band.profile.enums.BandStatus.RECRUITING
""")
    List<Band> findAllRecruitingNotManagedBy(@Param("memberId") Long memberId);

    @Query("""
        SELECT DISTINCT b
        FROM Band b
        LEFT JOIN FETCH b.bandSessions bs
        LEFT JOIN FETCH bs.session s
        LEFT JOIN FETCH b.representativeTrack rt
        WHERE b.id = :bandId
    """)
    Optional<Band> findBandDetailById(@Param("bandId") Long bandId);
}

