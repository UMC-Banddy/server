package com.umc.banddy.domain.music.track.web.controller;

import com.umc.banddy.domain.music.track.service.TrackService;
import com.umc.banddy.domain.music.track.web.dto.TrackRequestDto;
import com.umc.banddy.domain.music.track.web.dto.TrackResponseDto;
import com.umc.banddy.domain.music.track.web.dto.TrackToggleRequestDto;
import com.umc.banddy.domain.music.track.web.dto.TrackToggleResponseDto;
import com.umc.banddy.global.apiPayload.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;

import java.util.List;

import static com.umc.banddy.global.security.jwt.JwtTokenUtil.extractToken;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

   // 곡 저장
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
    @GetMapping
    public ResponseEntity<ApiResponse<List<TrackResponseDto.TrackResultDto>>> getAllTracks(HttpServletRequest request) {
        String token = extractToken(request);
        List<TrackResponseDto.TrackResultDto> tracks = trackService.getAllTracks(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(tracks));
    }

    // 상세 조회
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
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<TrackToggleResponseDto>> toggleTrack(
            @RequestBody TrackToggleRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = extractToken(request);
        TrackToggleResponseDto result = trackService.toggleTrack(requestDto.getSpotifyId(), token);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }



}
