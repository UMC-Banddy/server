package com.umc.banddy.domain.chat.domain;

import com.umc.banddy.domain.band.profile.domain.mapping.BandChat;
import com.umc.banddy.domain.chat.domain.enums.RoomType;
import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@Builder
@Setter
@NoArgsConstructor
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String name; // 그룹채팅에서만 사용

    @Column(nullable = true)
    private String imageUrl; // 그룹 썸네일 용도

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_room_id")
    private List<ChatRoomParticipant> participants;

    @OneToOne(mappedBy = "chatRoom", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private BandChat bandChat;

}
