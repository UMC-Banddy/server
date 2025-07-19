package com.umc.banddy.domain.chat.web.dto;

import com.umc.banddy.domain.band.profile.enums.BandStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class RecruitmentUpdateRequest {

    private Long bandId;
    private BandStatus status;
    private String profileImageUrl;
    private String representativeSong;
    private String name;
    private LocalDateTime endDate;
    private Boolean autoClose;
    private String description;

    private List<String> session;
    private List<Long> genre;
    private List<Long> artist;
    private List<Long> track;

    private Integer ageStart;
    private Integer ageEnd;
    private String gender;
    private String region;
    private String district;
    private String averageAge;
    private List<String> job;
    private Integer maleCount;
    private Integer femaleCount;

    private List<String> currentSessions;
    private Map<String, String> snsLinks;
}
