package com.umc.banddy.domain.band.profile.web.dto.Recruitment;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BandApplicationResponse {


    private Long roomId;

    private Long bandId;

    private String bandName;

    private String bandImageUrl;

    private String managerName;

    private String managerImageUrl;

    private LocalDateTime createdAt;
}
