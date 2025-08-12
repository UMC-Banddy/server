package com.umc.banddy.domain.band.profile.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BandSuggestionResponse {
    private String suggestion;
    private String genre;       // 장르 (없으면 null)
    private String artistName;
}
