package com.umc.banddy.domain.band.profile.web.dto.Recruitment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class ApplicantUpdateRequest {

    @NotEmpty
    private List<ApplicantUpdateDto> applicantUpdate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "단일 지원자 업데이트 정보")
    public static class ApplicantUpdateDto {

        @NotNull
        @Schema(description = "채팅방 ID", example = "15")
        private Long roomId;

        @NotNull
        @Schema(
                description    = "지원 상태",
                allowableValues = {"PASS", "FAIL"},
                example        = "FAIL"
        )
        private String status;
    }

}
