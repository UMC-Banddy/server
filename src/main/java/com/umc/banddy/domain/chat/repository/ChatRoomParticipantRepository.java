package com.umc.banddy.domain.chat.repository;

import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    @Query("""
      select distinct p
      from ChatRoomParticipant p
      join fetch p.chatRoom r
      join fetch r.participants rp
      left  join fetch r.bandChat   bc
      where p.member   = :member
        and r.roomType in (
          com.umc.banddy.domain.chat.domain.enums.RoomType.GROUP,
          com.umc.banddy.domain.chat.domain.enums.RoomType.BAND
        )
    """)
    List<ChatRoomParticipant> findAllGroupAndBandWithRoomParticipantsAndBandChatByMember(
            @Param("member") Member member
    );

    @Query("""
        SELECT cp2
        FROM ChatRoomParticipant cp1
             JOIN cp1.chatRoom cr
             JOIN cr.participants cp2
        WHERE cp1.member.id    = :memberId
          AND cp2.member.id   IN :friendIds
          AND cr.roomType       = com.umc.banddy.domain.chat.domain.enums.RoomType.PRIVATE
    """)
    List<ChatRoomParticipant> findFriendParticipants(
            @Param("memberId")  Long memberId,
            @Param("friendIds") List<Long> friendIds
    );

    Optional<ChatRoomParticipant> findByChatRoomAndMemberAndStatus(ChatRoom chatRoom, Member member, Status status);

    @Query("""
    SELECT cp.member.email
    FROM ChatRoomParticipant cp
    WHERE cp.chatRoom.id = :roomId
      AND cp.status = 'ACTIVE'
    """)
    List<String> findActiveEmailsByRoomId(@Param("roomId") Long roomId);

    Optional<ChatRoomParticipant> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    List<ChatRoomParticipant> findAllByChatRoom(ChatRoom chatRoom);
}
