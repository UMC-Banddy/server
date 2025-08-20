package com.umc.banddy.domain.member.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSurveyResponse {

    // 기본 회원 정보
    private String nickname;
    private Integer age;
    private String gender;
    private String region;   // 시 단위만
    private String bio;

    // 사전테스트 정보
    private List<String> genres;
    private List<String> artists;
    private List<SessionDto> sessions;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionDto {
        private String name;   // 세션명
        private String level;  // 레벨
    }
}
