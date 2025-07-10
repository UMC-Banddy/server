package com.umc.banddy.domain.music.track.web.controller;

import com.umc.banddy.domain.music.track.service.TrackService;
import com.umc.banddy.domain.music.track.web.dto.TrackRequestDto;
import com.umc.banddy.domain.music.track.web.dto.TrackResponseDto;
import com.umc.banddy.domain.music.track.web.dto.TrackToggleRequestDto;
import com.umc.banddy.domain.music.track.web.dto.TrackToggleResponseDto;
import com.umc.banddy.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

    // 곡 저장
    @PostMapping
    public ResponseEntity<ApiResponse<TrackResponseDto.TrackResultDto>> saveTrack(
            @RequestBody TrackRequestDto.TrackSaveDto requestDto
    ) {
        TrackResponseDto.TrackResultDto result = trackService.saveTrack(requestDto);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    // 곡 삭제
    @DeleteMapping("/{trackId}")
    public ResponseEntity<ApiResponse<Void>> deleteTrack(
            @PathVariable Long trackId
    ) {
        trackService.deleteTrack(trackId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 내 저장곡 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<TrackResponseDto.TrackResultDto>>> getAllTracks() {
        List<TrackResponseDto.TrackResultDto> tracks = trackService.getAllTracks();
        return ResponseEntity.ok(ApiResponse.onSuccess(tracks));
    }


    // 상세 조회
    @GetMapping("/{trackId}")
    public ResponseEntity<ApiResponse<TrackResponseDto.TrackResultDto>> getTrack(
            @PathVariable Long trackId
    ) {
        TrackResponseDto.TrackResultDto track = trackService.getTrackByTrackId(trackId);
        return ResponseEntity.ok(ApiResponse.onSuccess(track));
    }

    // 토글
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<TrackToggleResponseDto>> toggleTrack(
            @RequestBody TrackToggleRequestDto requestDto
    ) {
        TrackToggleResponseDto result = trackService.toggleTrack(requestDto.getSpotifyId());
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }


}
