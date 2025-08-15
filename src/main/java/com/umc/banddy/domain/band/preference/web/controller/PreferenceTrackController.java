package com.umc.banddy.domain.band.preference.web.controller;

import com.umc.banddy.domain.band.preference.service.PreferenceTrackService;
import com.umc.banddy.domain.band.preference.web.dto.PreferenceTrackResponse;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/tracks/preferences")
@RequiredArgsConstructor
public class PreferenceTrackController {

    private final PreferenceTrackService preferenceTrackService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping
    public PreferenceTrackResponse getPreferredTracks(HttpServletRequest request) {
        Long memberId = extractMemberIdOrThrow(request);
        return preferenceTrackService.getPreferences(memberId);
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
}
