package com.umc.banddy.domain.chat.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.banddy.domain.chat.domain.QChatMessage;
import com.umc.banddy.domain.chat.web.dto.Message.CursorChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessagePagingRepository {

    private final JPAQueryFactory queryFactory;

    public List<CursorChatMessage> findByRoomIdWithCursorAsDto(Long roomId, Long cursor, int limit) {
        return queryFactory
                .select(Projections.constructor(
                        CursorChatMessage.class,
                        QChatMessage.chatMessage.id,
                        QChatMessage.chatMessage.member.id,
                        QChatMessage.chatMessage.member.nickname,
                        QChatMessage.chatMessage.content,
                        QChatMessage.chatMessage.createdAt
                ))
                .from(QChatMessage.chatMessage)
                .where(
                        QChatMessage.chatMessage.chatRoom.id.eq(roomId),
                        cursor != null ? QChatMessage.chatMessage.id.lt(cursor) : null
                )
                .orderBy(QChatMessage.chatMessage.id.asc())
                .limit(limit + 1)
                .fetch();
    }

    private BooleanExpression ltCursor(Long cursor) {
        return cursor != null ? QChatMessage.chatMessage.id.lt(cursor) : null;
    }
}
