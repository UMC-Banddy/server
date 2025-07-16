package com.umc.banddy.domain.member.web.controller;

import com.umc.banddy.domain.member.domain.Genre;
import com.umc.banddy.domain.member.service.MemberSurveyService;
import com.umc.banddy.domain.member.web.dto.MemberSurveyRequest;
import com.umc.banddy.domain.music.artist.domain.Artist;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.umc.banddy.domain.member.enums.KeywordCategory;
import com.umc.banddy.domain.member.web.dto.SimpleKeywordDto;
import com.umc.banddy.domain.member.web.dto.SimpleSessionDto;

@Tag(name = "member-survey", description = "사전 테스트 API")
@RestController
@RequestMapping("/member/survey")
@RequiredArgsConstructor
public class MemberSurveyController {

    private final MemberSurveyService memberSurveyService;

    // 사전 테스트 정보 저장
    @Operation(summary = "사전 테스트 정보 저장")
    @PostMapping
    public ResponseEntity<Void> saveSurvey(@RequestBody MemberSurveyRequest request,
                                           @RequestHeader("Authorization") String accessToken) {
        memberSurveyService.saveSurveyInfo(accessToken, request);
        return ResponseEntity.ok().build();
    }

    // 사전 테스트 장르 조회
    @Operation(summary = "사전 테스트 장르 조회")
    @GetMapping("/genre")
    public ResponseEntity<List<Genre>> getGenres() {
        return ResponseEntity.ok(memberSurveyService.getAllGenres());
    }

    // 사전 테스트 아티스트 조회
    @Operation(summary = "사전 테스트 아티스트 조회")
    @GetMapping("/artist")
    public ResponseEntity<List<Artist>> getArtists() {
        return ResponseEntity.ok(memberSurveyService.getAllArtists());
    }
    @Operation(summary = "사전 테스트 장르 키워드 검색")
    @GetMapping("/genres/search")
    public ResponseEntity<List<Genre>> searchGenres(@RequestParam String keyword) {
        return ResponseEntity.ok(memberSurveyService.searchGenres(keyword));
    }

    @Operation(summary = "사전 테스트 아티스트 키워드 검색")
    @GetMapping("/artists/search")
    public ResponseEntity<List<Artist>> searchArtists(@RequestParam String keyword) {
        return ResponseEntity.ok(memberSurveyService.searchArtists(keyword));
    }

    @Operation(summary = "카테고리별 키워드 전체 조회")
    @GetMapping("/keyword")
    public ResponseEntity<Map<KeywordCategory, List<SimpleKeywordDto>>> getGroupedKeywords() {
        return ResponseEntity.ok(memberSurveyService.getGroupedKeywords());
    }

    @Operation(summary = "세션 전체 조회")
    @GetMapping("/session")
    public ResponseEntity<List<SimpleSessionDto>> getAllSessions() {
        return ResponseEntity.ok(memberSurveyService.getAllSessions());
    }

}
