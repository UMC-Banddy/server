package com.umc.banddy.domain.chat.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class RecruitmentRequest {

    private String bandImageUrl;
    private Long bandMusicId;
    private LocalDateTime recruitmentEndDate;
    private String recruitmentText;
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
    private List<String> snsUrls;

}
