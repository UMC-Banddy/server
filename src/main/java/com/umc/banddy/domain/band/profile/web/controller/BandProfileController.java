package com.umc.banddy.domain.band.profile.web.controller;

import com.umc.banddy.domain.band.profile.service.BandDetailService;
import com.umc.banddy.domain.band.profile.service.BandProfileService;
import com.umc.banddy.domain.band.profile.web.dto.BandProfileResponse;
import com.umc.banddy.domain.band.profile.web.dto.BandDetailResponse;
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

    // 밴드 프로필 조회
    @GetMapping("/{bandId}/profile")
    public BandProfileResponse getBandProfile(
            @PathVariable Long bandId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return bandProfileService.getBandProfile(bandId, currentMemberId);
    }

    // 밴드 상세정보 조회
    @GetMapping("/{bandId}/detail")
    public ResponseEntity<BandDetailResponse> getBandDetail(
            @PathVariable Long bandId,
            HttpServletRequest request
    ) {
        Long loginMemberId = jwtTokenUtil.getMemberIdFromToken(JwtTokenUtil.extractToken(request));
        BandDetailResponse response = bandDetailService.getBandDetail(loginMemberId, bandId);
        return ResponseEntity.ok(response);
    }

}
