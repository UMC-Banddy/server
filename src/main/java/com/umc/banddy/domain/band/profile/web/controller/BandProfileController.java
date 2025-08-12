package com.umc.banddy.domain.band.profile.web.controller;

import com.umc.banddy.domain.band.profile.service.BandDetailService;
import com.umc.banddy.domain.band.profile.service.BandProfileService;
import com.umc.banddy.domain.band.profile.service.BandSuggestionService;
import com.umc.banddy.domain.band.profile.web.dto.BandProfileResponse;
import com.umc.banddy.domain.band.profile.web.dto.BandDetailResponse;
import com.umc.banddy.domain.band.profile.web.dto.BandSuggestionResponse;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/band")
@RequiredArgsConstructor
public class BandProfileController {

    private final BandProfileService bandProfileService;
    private final BandDetailService bandDetailService;
    private final JwtTokenUtil jwtTokenUtil;
    private final BandSuggestionService bandSuggestionService;

    // 밴드 프로필 조회
    @GetMapping("/{bandId}/profile")
    public BandProfileResponse getBandProfile(@PathVariable Long bandId, HttpServletRequest request) {
        validateIdOrThrow(bandId);
        Long currentMemberId = extractMemberIdOrThrow(request);
        return bandProfileService.getBandProfile(bandId, currentMemberId);
    }

    // 밴드 상세정보 조회
    @GetMapping("/{bandId}/detail")
    public ResponseEntity<BandDetailResponse> getBandDetail(@PathVariable Long bandId, HttpServletRequest request) {
        validateIdOrThrow(bandId);
        Long loginMemberId = extractMemberIdOrThrow(request);
        BandDetailResponse response = bandDetailService.getBandDetail(loginMemberId, bandId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bandId}/question")
    public BandSuggestionResponse getBandSuggestion(@PathVariable Long bandId) {
        validateIdOrThrow(bandId);
        return bandSuggestionService.getSuggestion(bandId);
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
