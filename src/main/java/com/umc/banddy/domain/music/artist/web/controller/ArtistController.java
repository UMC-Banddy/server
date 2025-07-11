package com.umc.banddy.domain.music.artist.web.controller;

import com.umc.banddy.domain.music.artist.service.ArtistService;
import com.umc.banddy.domain.music.artist.web.dto.ArtistRequestDto;
import com.umc.banddy.domain.music.artist.web.dto.ArtistResponseDto;
import com.umc.banddy.global.apiPayload.ApiResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    // 아티스트 저장
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
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<ArtistResponseDto>> toggleArtist(
            @RequestBody ArtistRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        ArtistResponseDto result = artistService.toggleArtist(requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    // 저장한 아티스트 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ArtistResponseDto>>> getSavedArtists(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        List<ArtistResponseDto> result = artistService.getSavedArtists(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    // 특정 아티스트 상세 조회
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
