package com.umc.banddy.domain.band.profile.repository;


import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.BandChat;
import com.umc.banddy.domain.band.profile.web.dto.Recruitment.BandChatSummaryDto;
import com.umc.banddy.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BandChatRepository extends JpaRepository<BandChat, Long> {
    List<BandChat> findByBandId(Long bandId);

    @Query("""
    SELECT bc FROM BandChat bc
    JOIN ChatRoomParticipant cp ON cp.chatRoom = bc.chatRoom
    WHERE bc.band = :band AND cp.member = :manager
""")
    List<BandChat> findByBandAndManagerParticipant(@Param("band") Band band, @Param("manager") Member manager);


}
