package com.umc.banddy.domain.chat.domain.Recruitment;

import com.umc.banddy.domain.member.domain.Genre;
import com.umc.banddy.domain.music.artist.domain.Artist;
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
public class RecruitmentArtist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recruitment_room_id", nullable = false)
    private RecruitmentRoom recruitmentRoom;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;
}
