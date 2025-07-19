package com.umc.banddy.domain.chat.domain.Recruitment;

import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@Builder
@Setter
@NoArgsConstructor(access = PROTECTED)
public class RecruitmentSNS extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String platform;

    @Column(nullable = false)
    private String snsUrl;

    @ManyToOne
    @JoinColumn(name = "recruitment_room_id", nullable = false)
    private RecruitmentRoom recruitmentRoom;

}
