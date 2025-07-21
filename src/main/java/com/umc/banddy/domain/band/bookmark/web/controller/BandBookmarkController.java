package com.umc.banddy.domain.band.bookmark.web.controller;

import com.umc.banddy.domain.band.bookmark.service.BandBookmarkService;
import com.umc.banddy.domain.band.bookmark.web.dto.BandBookmarkResponse;
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

    // 1. 밴드 저장
    @PostMapping("/{bandId}/bookmark")
    public ResponseEntity<Map<String, Object>> saveBookmark(@PathVariable Long bandId, HttpServletRequest request) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(JwtTokenUtil.extractToken(request));
        bandBookmarkService.save(memberId, bandId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "밴드가 저장되었습니다!"
        ));
    }

    // 2. 저장한 밴드 목록 조회
    @GetMapping("/bookmarks")
    public ResponseEntity<List<BandBookmarkResponse>> getBookmarks(HttpServletRequest request) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(JwtTokenUtil.extractToken(request));
        List<BandBookmarkResponse> response = bandBookmarkService.getBookmarks(memberId);
        return ResponseEntity.ok(response);
    }

    // 3. 저장한 밴드 삭제
    @DeleteMapping("/{bandId}/bookmark")
    public ResponseEntity<Map<String, Object>> deleteBookmark(@PathVariable Long bandId, HttpServletRequest request) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(JwtTokenUtil.extractToken(request));
        bandBookmarkService.delete(memberId, bandId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "저장한 밴드가 삭제되었습니다."
        ));
    }
}
