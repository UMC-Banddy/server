package com.umc.banddy.domain.chat.converter;

import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.enums.RoomType;
import com.umc.banddy.domain.chat.web.dto.message.ChatMessageResponse;
import com.umc.banddy.domain.chat.web.dto.MessageType;
import com.umc.banddy.domain.chat.web.dto.TimeMark;
import com.umc.banddy.domain.chat.web.dto.WsMessage;
import com.umc.banddy.domain.chat.web.dto.message.UnreadResponseImpl;
import com.umc.banddy.domain.chat.web.dto.message.UnreadPrivateResponseImpl;
import com.umc.banddy.domain.chat.web.dto.message.UnreadResponse;
import com.umc.banddy.domain.member.domain.Member;

import java.time.LocalDateTime;

public class ChatConveter {

    public static ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage){
        return ChatMessageResponse.builder()
                .messageId(chatMessage.getId())
                .senderId(chatMessage.getMember().getId())
                .senderName(chatMessage.getMember().getNickname())
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .roomId(chatMessage.getChatRoom().getId())
                .timestamp(chatMessage.getCreatedAt())
                .build();
    }

//    public static ChatMessage toChatMessage(Member member, ChatRoom chatRoom, String content) {
//        return ChatMessage.builder()
//                .member(member)
//                .chatRoom(chatRoom)
//                .content(content)
//                .build();
//    }

    public static <M> WsMessage<M> toWsMessage(M message, MessageType type){
        return  WsMessage.<M>builder()
                .type(type)
                .data(message)
                .build();
    }

    public static TimeMark toTimeMark(ChatRoom chatRoom, Member member) {
        return TimeMark.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .roomId(chatRoom.getId())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static UnreadResponse toUnreadResponse(
            Member member,
            ChatRoom chatRoom,
            ChatMessage chatMessage,
            RoomType type
    ) {
        if (RoomType.PRIVATE.equals(type) || RoomType.GROUP.equals(type)) {
            return UnreadPrivateResponseImpl.builder()
                    .senderId(member.getId())
                    .roomId(chatRoom.getId())
                    .content(chatMessage.getContent())
                    .timestamp(chatMessage.getCreatedAt())
                    .build();
        } else if (RoomType.BAND.equals(type)) {
            return UnreadResponseImpl.builder()
                    .senderId(member.getId())
                    .roomId(chatRoom.getId())
                    .bandId(chatRoom.getBandChat().getBand().getId())
                    .content(chatMessage.getContent())
                    .timestamp(chatMessage.getCreatedAt())
                    .build();
        } else{
            throw new IllegalArgumentException("잘못된 채팅 타입: " + type);
        }
    }

}
