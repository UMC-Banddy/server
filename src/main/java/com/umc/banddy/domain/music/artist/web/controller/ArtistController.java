package com.umc.banddy.domain.music.artist.web.controller;

import com.umc.banddy.domain.music.artist.service.ArtistService;
import com.umc.banddy.domain.music.artist.web.dto.ArtistRequestDto;
import com.umc.banddy.domain.music.artist.web.dto.ArtistResponseDto;
import com.umc.banddy.domain.music.artist.web.dto.ArtistToggleResponseDto;
import com.umc.banddy.global.apiPayload.ApiResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "아티스트 아카이빙", description = "아티스트 아카이빙 관련 API")
@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    // 아티스트 저장
    @Operation(summary = "아티스트 저장", description = "아카이브에 아티스트를 저장합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ArtistResponseDto>> saveArtist(
            @RequestBody ArtistRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        ArtistResponseDto result = artistService.saveArtist(requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    // 아티스트 삭제
    @Operation(summary = "아티스트 삭제", description = "아카이브에서 아티스트를 삭제합니다.")
    @DeleteMapping("/{artistId}")
    public ResponseEntity<ApiResponse<Void>> deleteArtist(
            @PathVariable Long artistId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        artistService.deleteArtist(artistId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 아티스트 저장/삭제 토글
    @Operation(summary = "아티스트 저장/삭제", description = "토글 방식으로 아카이브에 아티스트를 저장 및 삭제합니다.")
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<ArtistToggleResponseDto>> toggleArtist(
            @RequestBody ArtistRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        ArtistToggleResponseDto result = artistService.toggleArtist(requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    // 저장한 아티스트 목록 조회
    @Operation(summary = "아티스트 목록 조회", description = "아카이브에 저장한 아티스트 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ArtistResponseDto>>> getSavedArtists(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        List<ArtistResponseDto> result = artistService.getSavedArtists(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    // 특정 아티스트 상세 조회
    @Operation(summary = "아티스트 상세 조회", description = "아카이브에 저장한 특정 아티스트를 상세 조회합니다.")
    @GetMapping("/{artistId}")
    public ResponseEntity<ApiResponse<ArtistResponseDto>> getArtistDetail(
            @PathVariable Long artistId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        ArtistResponseDto result = artistService.getArtistDetail(artistId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }
}
