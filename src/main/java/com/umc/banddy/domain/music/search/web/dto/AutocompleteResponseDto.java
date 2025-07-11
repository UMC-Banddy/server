package com.umc.banddy.domain.music.search.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AutocompleteResponseDto {
    private List<String> results;
}
