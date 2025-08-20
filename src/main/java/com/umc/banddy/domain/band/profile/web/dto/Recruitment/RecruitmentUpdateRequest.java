package com.umc.banddy.domain.band.profile.web.dto.Recruitment;

import com.umc.banddy.domain.band.profile.enums.BandStatus;
import com.umc.banddy.domain.band.profile.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class RecruitmentUpdateRequest {

    @Positive
    private Long bandId;
    private BandStatus status;
//    @Schema(
//            description = "업데이트할 프로필 이미지(선택)",
//            type        = "string",
//            format      = "binary"
//    )
//    private MultipartFile image;

    @Size(min = 22, max = 22, message = "Spotify ID는 22자여야 합니다.")
    private String representativeSong;
    private String name;
    private LocalDateTime endDate;
    private Boolean autoClose;

    @Size(max = 100, message = "밴드 설명은 최대 100자까지 입력할 수 있습니다.")
    private String description;

    private List<String> session;
    private List<String> genres;
    private List<@Size(min = 22, max = 22)String> artistSpotifyIds;
    private List<@Size(min = 22, max = 22)String> trackSpotifyIds;

    private String fileUrl;          // 업로드된 파일 S3 URL
    private String originalFilename; // 사용자가 업로드한 원래 이름

    @Positive
    private Integer ageStart;
    @Positive
    private Integer ageEnd;
    private String gender;
    private String region;
    private String district;
    private String averageAge;
    private List<String> job;
    @Positive
    private Integer maleCount;
    @Positive
    private Integer femaleCount;

    private List<String> currentSessions;
    private Map<String, String> snsLinks;
}
