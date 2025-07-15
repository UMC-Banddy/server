package com.umc.banddy.domain.other.profile.domain.mapping;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSnsId implements Serializable {
    private Long id;
    private Long memberId;
}
