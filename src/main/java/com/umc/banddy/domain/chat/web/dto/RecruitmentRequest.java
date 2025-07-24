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
public class RecruitmentRequest {

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
