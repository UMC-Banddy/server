package com.umc.banddy.domain.band.preference.web.controller;

import com.umc.banddy.domain.band.preference.service.PreferenceTrackService;
import com.umc.banddy.domain.band.preference.web.dto.PreferenceTrackResponse;
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
        String token = jwtTokenUtil.extractToken(request);
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        return preferenceTrackService.getPreferences(memberId);
    }
}
