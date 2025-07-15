package com.umc.banddy.domain.other.profile.domain.mapping;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_sns")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@IdClass(MemberSnsId.class)
public class MemberSns {
    @Id
    private Long id;

    @Id
    @Column(name = "id2")
    private Long memberId;

    @Column(name = "sns_name")
    private String snsName;

    @Column(name = "sns_url")
    private String snsUrl;
}