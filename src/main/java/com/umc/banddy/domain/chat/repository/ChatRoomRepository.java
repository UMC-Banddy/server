package com.umc.banddy.domain.chat.repository;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.enums.RoomType;
import com.umc.banddy.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByParticipants_MemberAndRoomType(Member member, RoomType roomType);

    @Query("""
        SELECT cr FROM ChatRoom cr
        WHERE cr.roomType = 'PRIVATE'
        AND cr.id IN (
            SELECT cp1.chatRoom.id FROM ChatRoomParticipant cp1
            JOIN ChatRoomParticipant cp2 ON cp1.chatRoom.id = cp2.chatRoom.id
            WHERE cp1.member.id = :memberId1 AND cp2.member.id = :memberId2
        )
    """)
    Optional<ChatRoom> findPrivateChatRoomByParticipants(@Param("memberId1") Long memberId1, @Param("memberId2") Long memberId2);


}
