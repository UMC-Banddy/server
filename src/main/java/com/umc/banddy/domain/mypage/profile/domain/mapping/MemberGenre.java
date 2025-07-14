package com.umc.banddy.domain.mypage.profile.domain.mapping;

import com.umc.banddy.domain.mypage.profile.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_genre")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberGenre {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id2") // member_id 역할
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;
}
