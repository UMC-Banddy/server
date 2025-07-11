package com.umc.banddy.domain.music.folder.web.controller;

import com.umc.banddy.domain.music.artist.web.dto.ArtistResponseDto;
import com.umc.banddy.domain.music.folder.service.ArtistFolderService;
import com.umc.banddy.domain.music.folder.web.dto.FolderArtistsRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderArtistsResponseDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderResponseDto;
import com.umc.banddy.global.apiPayload.ApiResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist-folders")
@RequiredArgsConstructor
public class ArtistFolderController {

    private final ArtistFolderService artistFolderService;

    // 폴더 생성
    @PostMapping
    public ResponseEntity<ApiResponse<FolderResponseDto>> createFolder(
            @RequestBody FolderRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        FolderResponseDto responseDto = artistFolderService.createFolder(requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }

    // 폴더 삭제
    @DeleteMapping("/{folderId}")
    public ResponseEntity<ApiResponse<Void>> deleteFolder(
            @PathVariable Long folderId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        artistFolderService.deleteFolder(folderId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 폴더 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<FolderResponseDto>>> getFoldersByMember(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        List<FolderResponseDto> folders = artistFolderService.getFoldersByMember(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(folders));
    }

    // 폴더에 아티스트 추가
    @PostMapping("/{folderId}/artists")
    public ResponseEntity<ApiResponse<FolderArtistsResponseDto>> addArtistToFolder(
            @PathVariable Long folderId,
            @RequestBody FolderArtistsRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        FolderArtistsResponseDto responseDto = artistFolderService.addArtistToFolder(folderId, requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }

    // 폴더 내 아티스트 삭제
    @DeleteMapping("/{folderId}/artists/{artistId}")
    public ResponseEntity<ApiResponse<Void>> removeArtistFromFolder(
            @PathVariable Long folderId,
            @PathVariable Long artistId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        artistFolderService.removeArtistFromFolder(folderId, artistId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 특정 폴더 내 아티스트 목록 조회
    @GetMapping("/{folderId}/artists")
    public ResponseEntity<ApiResponse<List<ArtistResponseDto>>> getArtistsInFolder(
            @PathVariable Long folderId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        List<ArtistResponseDto> artists = artistFolderService.getArtistsInFolder(folderId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(artists));
    }
}
