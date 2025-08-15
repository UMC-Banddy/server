package com.umc.banddy.domain.mypage.similarartist.web.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtistSuggestionQuestionResponse {
    private String question;
    private String artistName;
    private String memberNickname;
}
