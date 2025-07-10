package com.umc.banddy.domain.mypage.profile.domain.mapping;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "keyword")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Keyword {

    @Id
    private Long id;

    private String content;

    private String category;
}
