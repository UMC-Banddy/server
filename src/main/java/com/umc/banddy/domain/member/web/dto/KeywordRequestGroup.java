package com.umc.banddy.domain.member.web.dto;

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
    private List<String> MANNER;
    private List<String> SKILL;
    private List<String> STYLE;
    private List<String> FREQ;
}
