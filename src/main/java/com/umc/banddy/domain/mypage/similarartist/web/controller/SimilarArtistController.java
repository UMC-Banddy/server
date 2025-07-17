package com.umc.banddy.domain.mypage.similarartist.web.controller;

import com.umc.banddy.domain.mypage.similarartist.service.SimilarArtistService;
import com.umc.banddy.domain.mypage.similarartist.web.dto.SimilarArtistResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
@Tag(name = "비슷한 유저 아티스트 추천")
public class SimilarArtistController {

    private final SimilarArtistService similarArtistService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/similar")
    public ResponseEntity<List<SimilarArtistResponse>> getSimilarArtists(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        if (token == null || token.isBlank()) return ResponseEntity.badRequest().build();

        Long memberId;
        try {
            memberId = jwtTokenUtil.getMemberIdFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        List<SimilarArtistResponse> response = similarArtistService.getArtistsSavedBySimilarUsers(memberId);
        return ResponseEntity.ok(response);
    }
}

