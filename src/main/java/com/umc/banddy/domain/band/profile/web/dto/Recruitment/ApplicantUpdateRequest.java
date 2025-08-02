package com.umc.banddy.domain.band.profile.web.dto.Recruitment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class ApplicantUpdateRequest {

    private Map<Long, String> applicantUpdate;

}
