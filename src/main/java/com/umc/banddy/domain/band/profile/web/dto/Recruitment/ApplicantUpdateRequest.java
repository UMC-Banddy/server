package com.umc.banddy.domain.band.profile.web.dto.Recruitment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ApplicantUpdateRequest {

    private List<ApplicantUpdate> applicantUpdate;

    public static class ApplicantUpdate{

        private Long memberId;
        private String Status;
    }

}
