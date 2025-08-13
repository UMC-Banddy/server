package com.umc.banddy.domain.band.profile.repository;

import com.umc.banddy.domain.band.profile.domain.mapping.BandSession;
import com.umc.banddy.domain.member.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BandSessionRepository extends JpaRepository<BandSession, Long> {
    List<BandSession> findByBandIdAndIsDeletedFalse(Long bandId);

    List<BandSession> findByBandIdAndSessionStatusAndIsDeletedFalse(Long bandId, String status);

    Optional<BandSession> findByBandIdAndSessionStatusAndSessionAndIsDeletedFalse(Long bandId, String recruiting, Session Session);

    @Query("""
    SELECT bs.session.name
    FROM BandSession bs
    WHERE bs.band.id = :bandId AND bs.sessionStatus = :status
    AND bs.isDeleted = false
    """)
    List<String> findSessionNamesByBandIdAndStatusAndIsDeletedFalse(
            @Param("bandId") Long bandId,
            @Param("status") String status
    );

}
