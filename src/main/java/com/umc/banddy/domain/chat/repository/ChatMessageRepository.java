package com.umc.banddy.domain.chat.repository;

import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    @Query("""
    SELECT cm
    FROM ChatMessage cm
    WHERE cm.chatRoom.id IN :roomIds
    AND cm.createdAt = (
        SELECT MAX(cm2.createdAt)
        FROM ChatMessage cm2
        WHERE cm2.chatRoom.id = cm.chatRoom.id
    )
    """)
    List<ChatMessage> findLastMessagePerChatRoom(@Param("roomIds") List<Long> roomIds);


    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId AND m.id > :lastReadMessageId")
    int countUnreadMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("lastReadMessageId") Long lastReadMessageId
    );

    interface LastMessageProjection {
        Long            getRoomId();
        LocalDateTime   getLastMessageAt();
    }


    @Query("""
        select r.id             as roomId,
               max(c.createdAt) as lastMessageAt
        from   ChatMessage c
            join   c.chatRoom r
            join   r.participants p
            where  p.member = :member
            group  by r.id
    """)
    List<LastMessageProjection> findLastMessageAtByMember(
            @Param("member") Member member
    );


    interface UnreadCountProjection {
        Long getRoomId();
        Long getUnreadCount();
    }

    @Query("""
        select r.id             as roomId,
               count(c)         as unreadCount
        from   ChatMessage c
        join   c.chatRoom r
        join   r.participants p
        where  p.member    = :member
          and  c.createdAt > p.lastReadAt
        group  by r.id
    """)
    List<UnreadCountProjection> findUnreadCountsByMember(
            @Param("member") Member member
    );
}
