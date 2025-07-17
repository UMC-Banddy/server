package com.umc.banddy.domain.mypage.similartrack.web.controller;

import com.umc.banddy.domain.mypage.similartrack.service.SimilarTrackService;
import com.umc.banddy.domain.mypage.similartrack.web.dto.SimilarTrackResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
@Tag(name = "비슷한 유저 곡 추천")
public class SimilarTrackController {

    private final SimilarTrackService similarTrackService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/similar")
    public ResponseEntity<List<SimilarTrackResponse>> getSimilarTracks(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        if (token == null || token.isBlank()) return ResponseEntity.badRequest().build();

        Long memberId;
        try {
            memberId = jwtTokenUtil.getMemberIdFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        List<SimilarTrackResponse> response = similarTrackService.getTracksSavedBySimilarUsers(memberId);
        return ResponseEntity.ok(response);
    }
}
