package com.umc.banddy.domain.chat.repository;

import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    @Modifying
    @Transactional
    @Query("""
        UPDATE ChatRoomParticipant p
           SET p.lastReadAt = :lastReadAt
         WHERE p.chatRoom.id = :roomId
           AND p.member.id   = :memberId
        """)
    int updateLastReadAt(
            @Param("roomId")     Long roomId,
            @Param("memberId")   Long memberId,
            @Param("lastReadAt") LocalDateTime lastReadAt
    );


    Optional<ChatRoomParticipant> findByChatRoomIdAndMemberId(Long roomId, Long memberId);
    Optional<ChatRoomParticipant> findByChatRoomIdAndEmail(Long roomId, String email);

}
