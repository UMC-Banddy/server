package com.umc.banddy.domain.music.album.web.controller;

import com.umc.banddy.domain.music.album.service.AlbumService;
import com.umc.banddy.domain.music.album.web.dto.AlbumRequestDto;
import com.umc.banddy.domain.music.album.web.dto.AlbumResponseDto;
import com.umc.banddy.domain.music.album.web.dto.AlbumToggleResponseDto;
import com.umc.banddy.global.apiPayload.ApiResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "앨범 아카이빙", description = "앨범 아카이빙 관련 API")
@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    // 앨범 저장
    @Operation(summary = "앨범 저장", description = "아카이브에 앨범을 저장합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<AlbumResponseDto>> saveAlbum(
            @RequestBody AlbumRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        AlbumResponseDto result = albumService.saveAlbum(requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    // 앨범 삭제
    @Operation(summary = "앨범 삭제", description = "아카이브에서 앨범을 삭제합니다.")
    @DeleteMapping("/{albumId}")
    public ResponseEntity<ApiResponse<Void>> deleteAlbum(
            @PathVariable Long albumId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        albumService.deleteAlbum(albumId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 앨범 저장/삭제 토글
    @Operation(summary = "앨범 저장/삭제", description = "토글 방식으로 아카이브에 앨범을 저장 및 삭제합니다.")
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<AlbumToggleResponseDto>> toggleAlbum(
            @RequestBody AlbumRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        AlbumToggleResponseDto result = albumService.toggleAlbum(requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    // 저장한 앨범 목록 조회
    @Operation(summary = "앨범 목록 조회", description = "아카이브에 저장한 앨범 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AlbumResponseDto>>> getSavedAlbums(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        List<AlbumResponseDto> result = albumService.getSavedAlbums(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    // 특정 앨범 상세 조회
    @Operation(summary = "앨범 상세 조회", description = "아카이브에 저장한 특정 앨범을 상세 조회합니다.")
    @GetMapping("/{albumId}")
    public ResponseEntity<ApiResponse<AlbumResponseDto>> getAlbumDetail(
            @PathVariable Long albumId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        AlbumResponseDto result = albumService.getAlbumDetail(albumId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }
}
