package com.umc.banddy.domain.mypage.profile.domain.mapping;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "track")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Track {

    @Id
    private Long id;

    private String title;

    @Column(name = "image_url")
    private String imageUrl;
}
