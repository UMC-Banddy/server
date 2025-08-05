package com.umc.banddy.domain.band.profile.web.dto.Recruitment;

import com.umc.banddy.domain.band.profile.enums.BandStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class RecruitmentRequest {

    private BandStatus status;

    private String representativeSong;
    private String name;
    private LocalDateTime endDate;
    private Boolean autoClose;
    private String description;

    private List<String> session;
    private List<String> genres;
    private List<String> artistSpotifyIds;
    private List<String> trackSpotifyIds;

    private int ageStart;
    private int ageEnd;
    private String gender;
    private String region;
    private String district;
    private String averageAge;
    private List<String> job;
    private int maleCount;
    private int femaleCount;

    private List<String> currentSessions;
    private Map<String, String> snsLinks;
}
