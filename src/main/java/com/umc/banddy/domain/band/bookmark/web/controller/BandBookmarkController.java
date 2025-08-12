package com.umc.banddy.domain.band.bookmark.web.controller;

import com.umc.banddy.domain.band.bookmark.service.BandBookmarkService;
import com.umc.banddy.domain.band.bookmark.web.dto.BandBookmarkResponse;
import com.umc.banddy.global.apiPayload.ApiResponse; // 사용 안 하지만 프로젝트 컨벤션에 맞춰 유지 가능
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bands")
public class BandBookmarkController {

    private final BandBookmarkService bandBookmarkService;
    private final JwtTokenUtil jwtTokenUtil;

    // 밴드 저장
    @PostMapping("/{bandId}/bookmark")
    public ResponseEntity<Map<String, Object>> saveBookmark(
            @PathVariable Long bandId,
            HttpServletRequest request
    ) {
        Long memberId = extractMemberIdOrThrow(request);
        validateIdOrThrow(bandId);

        bandBookmarkService.save(memberId, bandId);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "밴드가 저장되었습니다!"
        ));
    }

    // 저장한 밴드 정보 조회
    @GetMapping("/bookmarks")
    public ResponseEntity<List<BandBookmarkResponse>> getBookmarkedBands(HttpServletRequest request) {
        Long memberId = extractMemberIdOrThrow(request);

        List<BandBookmarkResponse> responses = bandBookmarkService.getBookmarkedBands(memberId);
        return ResponseEntity.ok(responses);
    }

    // 저장한 밴드 삭제
    @DeleteMapping("/{bandId}/bookmark")
    public ResponseEntity<Map<String, Object>> deleteBookmark(
            @PathVariable Long bandId,
            HttpServletRequest request
    ) {
        Long memberId = extractMemberIdOrThrow(request);
        validateIdOrThrow(bandId);

        bandBookmarkService.delete(memberId, bandId);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "저장한 밴드가 삭제되었습니다."
        ));
    }

    private Long extractMemberIdOrThrow(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        if (token == null || token.isBlank()) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }
        try {
            return jwtTokenUtil.getMemberIdFromToken(token);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }
    }

    private void validateIdOrThrow(Long id) {
        if (id == null || id <= 0) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
    }
}
