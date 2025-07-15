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
public class MemberSurveyRequest {

    private List<String> genreNames;
    private KeywordRequestGroup keywords;
    private List<Long> artistIds;
    private List<SessionRequest> sessions;
    private List<SnsLinkRequest> snsLinks;

    private String profileImageUrl;
    private String introduction;
    private String mediaUrl;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SessionRequest {
        private String sessionName;
        private String level;
    }
}
