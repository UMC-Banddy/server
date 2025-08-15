package com.umc.banddy.domain.music.track.web.controller;

import com.umc.banddy.domain.music.track.service.TrackService;
import com.umc.banddy.domain.music.track.web.dto.TrackRequestDto;
import com.umc.banddy.domain.music.track.web.dto.TrackResponseDto;
import com.umc.banddy.domain.music.track.web.dto.TrackToggleRequestDto;
import com.umc.banddy.domain.music.track.web.dto.TrackToggleResponseDto;
import com.umc.banddy.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;

import java.util.List;

import static com.umc.banddy.global.security.jwt.JwtTokenUtil.extractToken;

@Tag(name = "곡 아카이빙", description = "곡 아카이브 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;
    private final JwtTokenUtil jwtTokenUtil;

   // 곡 저장
   @Operation(summary = "곡 저장", description = "아카이브에 곡을 저장합니다.")
   @PostMapping
    public ResponseEntity<ApiResponse<TrackResponseDto.TrackResultDto>> saveTrack(
            @RequestBody TrackRequestDto.TrackSaveDto requestDto,
            HttpServletRequest request
    ) {
        String token = extractToken(request);
        TrackResponseDto.TrackResultDto result = trackService.saveTrack(requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }


    // 곡 삭제
    @Operation(summary = "곡 삭제", description = "아카이브에서 곡을 삭제합니다.")
    @DeleteMapping("/{trackId}")
    public ResponseEntity<ApiResponse<Void>> deleteTrack(
            @PathVariable Long trackId,
            HttpServletRequest request
    ) {
        String token = extractToken(request);
        trackService.deleteTrack(trackId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }


    // 내 저장곡 목록 조회
    @Operation(summary = "곡 목록 조회", description = "아카이브에 저장한 곡 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TrackResponseDto.TrackResultDto>>> getAllTracks(HttpServletRequest request) {
        String token = extractToken(request);
        List<TrackResponseDto.TrackResultDto> tracks = trackService.getAllTracks(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(tracks));
    }

    // 상세 조회
    @Operation(summary = "곡 상세 조회", description = "아카이브에 저장한 특정 곡을 상세 조회합니다.")
    @GetMapping("/{trackId}")
    public ResponseEntity<ApiResponse<TrackResponseDto.TrackResultDto>> getTrack(
            @PathVariable Long trackId,
            HttpServletRequest request
    ) {
        String token = extractToken(request);
        TrackResponseDto.TrackResultDto track = trackService.getTrackByTrackId(trackId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(track));
    }

    // 토글
    @Operation(summary = "곡 저장/삭제", description = "토글 방식으로 아카이브에 곡을 저장 및 삭제합니다.")
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<TrackToggleResponseDto>> toggleTrack(
            @RequestBody TrackToggleRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = extractToken(request);
        TrackToggleResponseDto result = trackService.toggleTrack(requestDto.getSpotifyId(), token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    // 마이페이지 - 최근 저장한 곡 조회
    @Operation(summary = "최근 저장한 곡 조회", description = "최근 아카이브에 저장한 곡을 조회합니다.")
    @GetMapping("/recent")
    public ResponseEntity<List<TrackResponseDto.TrackResultDto>> getRecentTracks(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        List<TrackResponseDto.TrackResultDto> response = trackService.getRecentTracks(token);
        return ResponseEntity.ok(response);
    }

}
