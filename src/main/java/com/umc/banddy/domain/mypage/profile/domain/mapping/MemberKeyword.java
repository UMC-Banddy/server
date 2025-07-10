package com.umc.banddy.domain.mypage.profile.domain.mapping;

import com.umc.banddy.domain.mypage.profile.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_keyword")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberKeyword {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;
}
