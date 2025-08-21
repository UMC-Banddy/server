package com.umc.banddy.domain.member.web.dto;

import com.umc.banddy.domain.member.enums.SessionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSurveyRequest {

    private List<String> genreNames;
    private KeywordRequestGroup keywords;
    private List<Long> artistIds;
    private List<SessionRequest> sessions;
    private List<SnsLinkRequest> snsLinks;

    private String profileImageUrl;
    private String bio;
    private String mediaUrl;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SessionRequest {
        private Long sessionId;   // 세션 아이디
        private String level;     // 레벨 (BEGINNER, INTERMEDIATE, ADVANCED 등)
        private SessionType sessionType;
    }

}
