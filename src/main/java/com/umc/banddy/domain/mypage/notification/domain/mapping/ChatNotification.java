package com.umc.banddy.domain.mypage.notification.domain.mapping;

import com.umc.banddy.domain.member.domain.Member;
//import com.umc.banddy.domain.chat.domain.ChatMessage;
//import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Enumerated(EnumType.STRING)
//    private ReadStatus isRead;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "chat_room_id")
//    private ChatRoom chatRoom;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "chat_message_id")
//    private ChatMessage chatMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;
}
