package com.umc.banddy.domain.band.profile.web.dto.Recruitment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BandApplicationResponse {

    private Long roomId;

    private String name;

    private String imageUrl;

    private LocalDateTime createdAt;
}
