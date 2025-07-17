package com.umc.banddy.domain.member.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeywordRequestGroup {

    @JsonProperty("manner")
    private List<String> MANNER;

    @JsonProperty("skill")
    private List<String> SKILL;

    @JsonProperty("style")
    private List<String> STYLE;

    @JsonProperty("freq")
    private List<String> FREQ;
}
