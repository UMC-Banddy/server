package com.umc.banddy.domain.other.profile.web.controller;

import com.umc.banddy.domain.music.album.web.dto.AlbumResponseDto;
import com.umc.banddy.domain.other.profile.service.OtherProfileService;
import com.umc.banddy.domain.other.profile.web.dto.MemberTagResponse;
import com.umc.banddy.domain.other.profile.web.dto.OtherProfileResponse;
import com.umc.banddy.domain.other.profile.web.dto.SavedTrackResponse;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "상대방 프로필 조회", description = "사용자 프로필 조회 관련 API")
public class OtherProfileController {

    private final OtherProfileService otherProfileService;
    private final JwtTokenUtil jwtTokenUtil;

    // 상대방 프로필 조회
    @GetMapping("/{memberId}/profile")
    public ResponseEntity<OtherProfileResponse> getOtherProfile(
            @PathVariable("memberId") Long targetMemberId,
            HttpServletRequest request
    ) {
        validateIdOrThrow(targetMemberId);
        Long loginMemberId = extractMemberIdOrThrow(request);
        OtherProfileResponse response = otherProfileService.getOtherProfile(loginMemberId, targetMemberId);
        return ResponseEntity.ok(response);
    }

    // 상대방 저장한 곡 조회
    @GetMapping("/{memberId}/profile/saved-tracks")
    public ResponseEntity<List<SavedTrackResponse>> getSavedTracks(
            @PathVariable("memberId") Long targetMemberId
    ) {
        validateIdOrThrow(targetMemberId);
        List<SavedTrackResponse> savedTracks = otherProfileService.getSavedTracks(targetMemberId);
        return ResponseEntity.ok(savedTracks);
    }

    // 상대방 태그 조회
    @GetMapping("/{memberId}/tags")
    public ResponseEntity<MemberTagResponse> getTagsByMemberId(
            @PathVariable("memberId") Long memberId
    ) {
        validateIdOrThrow(memberId);
        MemberTagResponse response = otherProfileService.getTagsByMemberId(memberId);
        return ResponseEntity.ok(response);
    }

    // 상대방 저장한 공개 앨범 조회
    @GetMapping("/{memberId}/profile/saved-albums")
    public ResponseEntity<List<AlbumResponseDto>> getSavedAlbums(
            @PathVariable("memberId") Long targetMemberId
    ) {
        validateIdOrThrow(targetMemberId);
        List<AlbumResponseDto> savedAlbums = otherProfileService.getSavedAlbums(targetMemberId);
        return ResponseEntity.ok(savedAlbums);
    }

    private Long extractMemberIdOrThrow(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        if (token == null || token.isBlank()) throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        try {
            return jwtTokenUtil.getMemberIdFromToken(token);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }
    }

    private void validateIdOrThrow(Long id) {
        if (id == null || id <= 0) throw new GeneralException(ErrorStatus._BAD_REQUEST);
    }
}
