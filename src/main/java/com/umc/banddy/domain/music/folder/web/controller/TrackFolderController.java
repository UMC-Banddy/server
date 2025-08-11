package com.umc.banddy.domain.music.folder.web.controller;

import com.umc.banddy.domain.music.folder.web.dto.FolderRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderResponseDto;
import com.umc.banddy.domain.music.folder.service.TrackFolderService;
import com.umc.banddy.domain.music.folder.web.dto.FolderTracksRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderTracksResponseDto;
import com.umc.banddy.domain.music.track.web.dto.TrackResponseDto;
import com.umc.banddy.global.apiPayload.ApiResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "아카이브 곡 폴더", description = "아카이브 곡 폴더 관련 API")
@RestController
@RequestMapping("/api/track-folders")
@RequiredArgsConstructor
public class TrackFolderController {

    private final TrackFolderService trackFolderService;

    // 폴더 생성
    @Operation(summary = "곡 폴더 생성", description = "아카이브에 곡 폴더를 생성합니다. (폴더 색상은 GRAY, YELLOW, GREEN, RED, ORANGE, BLUE만 가능)")
    @PostMapping
    public ResponseEntity<ApiResponse<FolderResponseDto>> createFolder(
            @RequestBody FolderRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        FolderResponseDto responseDto = trackFolderService.createFolder(requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }

    // 폴더 삭제
    @Operation(summary = "곡 폴더 삭제", description = "아카이브에서 곡 폴더를 삭제합니다.")
    @DeleteMapping("/{folderId}")
    public ResponseEntity<ApiResponse<FolderResponseDto>> deleteFolder(
            @PathVariable Long folderId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        trackFolderService.deleteFolder(folderId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 폴더에 곡 추가
    @Operation(summary = "폴더에 곡 추가", description = "아카이브 곡 폴더에 곡을 추가합니다.")
    @PostMapping("/{folderId}/tracks")
    public ResponseEntity<ApiResponse<FolderTracksResponseDto>> addTrackToFolder(
            @PathVariable Long folderId,
            @RequestBody FolderTracksRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        FolderTracksResponseDto responseDto = trackFolderService.addTrackToFolder(folderId, requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }


    // 폴더에서 곡 삭제
    @Operation(summary = "폴더에서 곡 삭제", description = "아카이브 곡 폴더에서 곡을 삭제합니다.")
    @DeleteMapping("/{folderId}/tracks/{trackId}")
    public ResponseEntity<ApiResponse<Void>> removeTrackFromFolder(
            @PathVariable Long folderId,
            @PathVariable Long trackId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        trackFolderService.removeTrackFromFolder(folderId, trackId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }


    // 폴더 내 곡 목록 조회
    @Operation(summary = "폴더 내 곡 목록 조회", description = "아카이브 곡 폴더 내 곡 목록을 조회합니다.")
    @GetMapping("/{folderId}/tracks")
    public ResponseEntity<ApiResponse<List<TrackResponseDto.TrackResultDto>>> getTracksInFolder(
            @PathVariable Long folderId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        List<TrackResponseDto.TrackResultDto> tracks = trackFolderService.getTracksInFolder(folderId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(tracks));
    }


    // 폴더 목록 조회
    @Operation(summary = "곡 폴더 목록 조회", description = "아카이브 곡 폴더 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<FolderResponseDto>>> getFoldersByMember(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        List<FolderResponseDto> folders = trackFolderService.getFoldersByMember(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(folders));
    }

    // 곡 폴더 수정
    @Operation(summary = "곡 폴더 수정", description = "곡 폴더의 이름, 색상을 수정합니다. (폴더 색상은 GRAY, YELLOW, GREEN, RED, ORANGE, BLUE만 가능)")
    @PatchMapping("/{folderId}")
    public ResponseEntity<ApiResponse<FolderResponseDto>> updateFolder(
            @PathVariable Long folderId,
            @RequestBody FolderRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        FolderResponseDto response = trackFolderService.updateFolder(folderId, requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
