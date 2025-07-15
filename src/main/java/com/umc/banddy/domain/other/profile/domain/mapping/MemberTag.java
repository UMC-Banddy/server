package com.umc.banddy.domain.other.profile.domain.mapping;

import com.umc.banddy.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberTag {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String tag;

    public String getTagName() {
        return this.tag;
    }
}
