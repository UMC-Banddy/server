package com.umc.banddy.domain.other.profile.web.controller;

import com.umc.banddy.domain.other.profile.service.OtherProfileService;
import com.umc.banddy.domain.other.profile.web.dto.OtherProfileResponse;
import com.umc.banddy.domain.other.profile.web.dto.SavedTrackResponse;
import com.umc.banddy.domain.other.profile.web.dto.MemberTagResponse;
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
        String token = JwtTokenUtil.extractToken(request);
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Long loginMemberId;
        try {
            loginMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        OtherProfileResponse response = otherProfileService.getOtherProfile(loginMemberId, targetMemberId);
        return ResponseEntity.ok(response);
    }

    // 상대방이 저장한 곡 목록 조회
    @GetMapping("/{memberId}/profile/saved-tracks")
    public ResponseEntity<List<SavedTrackResponse>> getSavedTracks(
            @PathVariable("memberId") Long targetMemberId
    ) {
        List<SavedTrackResponse> savedTracks = otherProfileService.getSavedTracks(targetMemberId);
        return ResponseEntity.ok(savedTracks);
    }

    // 상대방 태그 조회
    @GetMapping("/{memberId}/tags")
    public ResponseEntity<MemberTagResponse> getTagsByMemberId(
            @PathVariable("memberId") Long memberId
    ) {
        MemberTagResponse response = otherProfileService.getTagsByMemberId(memberId);
        return ResponseEntity.ok(response);
    }
}
