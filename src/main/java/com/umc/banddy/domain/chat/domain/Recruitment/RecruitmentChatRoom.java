package com.umc.banddy.domain.chat.domain.Recruitment;

import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.enums.PassFail;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@Builder
@Setter
public class RecruitmentChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private PassFail passFail = PassFail.PENDING;

    @ManyToOne
    @JoinColumn(name = "recruitment_room_id", nullable = false)
    private RecruitmentRoom recruitmentRoom;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
}
