package com.umc.banddy.domain.other.profile.domain.mapping;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.other.profile.domain.Tag;
import com.umc.banddy.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "member_tag",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "tag_id"})
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ AUTO_INCREMENT 매핑
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)   // ✅ NOT NULL 권장
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public String getTagName() {
        return this.tag != null ? this.tag.getName() : null;
    }

}
