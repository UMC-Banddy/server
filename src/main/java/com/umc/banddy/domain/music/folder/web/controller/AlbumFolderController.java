package com.umc.banddy.domain.music.folder.web.controller;

import com.umc.banddy.domain.music.album.web.dto.AlbumResponseDto;
import com.umc.banddy.domain.music.folder.service.AlbumFolderService;
import com.umc.banddy.domain.music.folder.web.dto.FolderAlbumsRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderAlbumsResponseDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderRequestDto;
import com.umc.banddy.domain.music.folder.web.dto.FolderResponseDto;
import com.umc.banddy.global.apiPayload.ApiResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "아카이브 앨범 폴더", description = "아카이브 앨범 폴더 관련 API")
@RestController
@RequestMapping("/api/album-folders")
@RequiredArgsConstructor
public class AlbumFolderController {

    private final AlbumFolderService albumFolderService;

    // 폴더 생성
    @Operation(summary = "앨범 폴더 생성", description = "아카이브에 앨범 폴더를 생성합니다. (폴더 색상은 GRAY, YELLOW, GREEN, RED, ORANGE, BLUE만 가능)")
    @PostMapping
    public ResponseEntity<ApiResponse<FolderResponseDto>> createFolder(
            @RequestBody FolderRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        FolderResponseDto responseDto = albumFolderService.createFolder(requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }

    // 폴더 삭제
    @Operation(summary = "앨범 폴더 삭제", description = "아카이브에서 앨범 폴더를 삭제합니다.")
    @DeleteMapping("/{folderId}")
    public ResponseEntity<ApiResponse<Void>> deleteFolder(
            @PathVariable Long folderId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        albumFolderService.deleteFolder(folderId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 폴더 목록 조회
    @Operation(summary = "앨범 폴더 목록 조회", description = "아카이브 앨범 폴더 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<FolderResponseDto>>> getFoldersByMember(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        List<FolderResponseDto> folders = albumFolderService.getFoldersByMember(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(folders));
    }

    // 폴더에 앨범 추가
    @Operation(summary = "폴더에 앨범 추가", description = "아카이브 앨범 폴더에 앨범을 추가합니다.")
    @PostMapping("/{folderId}/albums")
    public ResponseEntity<ApiResponse<FolderAlbumsResponseDto>> addAlbumToFolder(
            @PathVariable Long folderId,
            @RequestBody FolderAlbumsRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        FolderAlbumsResponseDto responseDto = albumFolderService.addAlbumToFolder(folderId, requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }

    // 폴더 내 앨범 삭제
    @Operation(summary = "폴더에서 앨범 삭제", description = "아카이브 앨범 폴더에서 앨범을 삭제합니다.")
    @DeleteMapping("/{folderId}/albums/{albumId}")
    public ResponseEntity<ApiResponse<Void>> removeAlbumFromFolder(
            @PathVariable Long folderId,
            @PathVariable Long albumId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        albumFolderService.removeAlbumFromFolder(folderId, albumId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 특정 폴더 내 앨범 목록 조회
    @Operation(summary = "폴더 내 앨범 목록 조회", description = "아카이브 앨범 폴더 내 앨범 목록을 조회합니다.")
    @GetMapping("/{folderId}/albums")
    public ResponseEntity<ApiResponse<List<AlbumResponseDto>>> getAlbumsInFolder(
            @PathVariable Long folderId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        List<AlbumResponseDto> albums = albumFolderService.getAlbumsInFolder(folderId, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(albums));
    }

    // 앨범 폴더 수정
    @Operation(summary = "앨범 폴더 수정", description = "앨범 폴더의 이름, 색상을 수정합니다. (폴더 색상은 GRAY, YELLOW, GREEN, RED, ORANGE, BLUE만 가능)")
    @PatchMapping("/{folderId}")
    public ResponseEntity<ApiResponse<FolderResponseDto>> updateFolder(
            @PathVariable Long folderId,
            @RequestBody FolderRequestDto requestDto,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        FolderResponseDto response = albumFolderService.updateFolder(folderId, requestDto, token);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
