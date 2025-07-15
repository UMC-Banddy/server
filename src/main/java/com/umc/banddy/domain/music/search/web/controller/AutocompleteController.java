package com.umc.banddy.domain.music.search.web.controller;

import com.umc.banddy.domain.music.search.service.AutocompleteService;
import com.umc.banddy.domain.music.search.web.dto.AutocompleteResponseDto;
import com.umc.banddy.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autocomplete")
@RequiredArgsConstructor
public class AutocompleteController {

    private final AutocompleteService autocompleteService;

    // 곡 자동완성
    @GetMapping("/tracks")
    public ResponseEntity<ApiResponse<AutocompleteResponseDto>> autocompleteTracks(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit
    ) {
        var results = autocompleteService.autocompleteTracks(query, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(new AutocompleteResponseDto(results)));
    }

    // 아티스트 자동완성
    @GetMapping("/artists")
    public ResponseEntity<ApiResponse<AutocompleteResponseDto>> autocompleteArtists(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit
    ) {
        var results = autocompleteService.autocompleteArtists(query, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(new AutocompleteResponseDto(results)));
    }

    // 앨범 자동완성
    @GetMapping("/albums")
    public ResponseEntity<ApiResponse<AutocompleteResponseDto>> autocompleteAlbums(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit
    ) {
        var results = autocompleteService.autocompleteAlbums(query, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(new AutocompleteResponseDto(results)));
    }

    // 통합 자동완성 (곡+아티스트+앨범)
    @GetMapping("/music")
    public ResponseEntity<ApiResponse<AutocompleteResponseDto>> autocompleteMusic(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit
    ) {
        var results = autocompleteService.autocompleteMusic(query, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(new AutocompleteResponseDto(results)));
    }
}
