package com.umc.banddy.domain.music.search.web.controller;

import com.umc.banddy.domain.music.search.service.AutocompleteService;
import com.umc.banddy.domain.music.search.web.dto.AutocompleteResponseDto;
import com.umc.banddy.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "음악 검색어 자동완성", description = "음악 검색 시 검색어 자동완성 API")
@RestController
@RequestMapping("/api/autocomplete")
@RequiredArgsConstructor
public class AutocompleteController {

    private final AutocompleteService autocompleteService;

    // 곡 자동완성
    @Operation(summary = "곡 검색어 자동완성", description = "입력한 검색어를 제목에 포함하는 곡 결과를 반환합니다.")
    @GetMapping("/tracks")
    public ResponseEntity<ApiResponse<AutocompleteResponseDto>> autocompleteTracks(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int limit
    ) {
        var results = autocompleteService.autocompleteTracks(query, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(new AutocompleteResponseDto(results)));
    }

    // 아티스트 자동완성
    @Operation(summary = "아티스트 검색어 자동완성", description = "입력한 검색어를 이름에 포함하는 아티스트 결과를 반환합니다.")
    @GetMapping("/artists")
    public ResponseEntity<ApiResponse<AutocompleteResponseDto>> autocompleteArtists(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int limit
    ) {
        var results = autocompleteService.autocompleteArtists(query, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(new AutocompleteResponseDto(results)));
    }

    // 앨범 자동완성
    @Operation(summary = "앨범 검색어 자동완성", description = "입력한 검색어를 제목에 포함하는 앨범 결과를 반환합니다.")
    @GetMapping("/albums")
    public ResponseEntity<ApiResponse<AutocompleteResponseDto>> autocompleteAlbums(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int limit
    ) {
        var results = autocompleteService.autocompleteAlbums(query, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(new AutocompleteResponseDto(results)));
    }

    // 통합 자동완성 (곡+아티스트+앨범)
    @Operation(summary = "음악 검색어 자동완성", description = "입력한 검색어를 제목이나 이름에 포함하는 곡, 아티스트, 앨범 결과를 반환합니다.")
    @GetMapping("/music")
    public ResponseEntity<ApiResponse<AutocompleteResponseDto>> autocompleteMusic(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit
    ) {
        var results = autocompleteService.autocompleteMusic(query, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(new AutocompleteResponseDto(results)));
    }
}
