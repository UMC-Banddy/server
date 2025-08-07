package com.umc.banddy.domain.band.profile.web.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BandDetailResponse {

    private Long bandId;
    private String bandName;
    private String profileImageUrl;

    private String ageRange;
    private String genderCondition;
    private String region;

    private String description;
    private String endDate;

    private List<SnsDto> snsList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SnsDto {
        private String platform;
        private String snsLink;
    }
}
