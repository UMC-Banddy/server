package com.umc.banddy.domain.music.folder.web.controller;

import com.umc.banddy.domain.music.folder.web.dto.FolderRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderResponseDto;
import com.umc.banddy.domain.music.folder.service.TrackFolderService;
import com.umc.banddy.domain.music.folder.web.dto.FolderTracksRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderTracksResponseDto;
import com.umc.banddy.domain.music.track.web.dto.TrackResponseDto;
import com.umc.banddy.global.apiPayload.ApiResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/track-folders")
@RequiredArgsConstructor
public class TrackFolderController {

    private final TrackFolderService trackFolderService;

    // 폴더 생성
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


    // 폴더에서 곡 삭제 (trackId 사용)
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
    @GetMapping
    public ResponseEntity<ApiResponse<List<FolderResponseDto>>> getFoldersByMember(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        List<FolderResponseDto> folders = trackFolderService.getFoldersByMember(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(folders));
    }

}
