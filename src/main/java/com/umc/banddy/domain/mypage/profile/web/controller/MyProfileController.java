package com.umc.banddy.domain.mypage.profile.web.controller;

import com.umc.banddy.domain.mypage.profile.service.MyProfileService;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileResponse;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileUpdateRequest;
import com.umc.banddy.global.apiPayload.ApiResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "마이페이지", description = "내 프로필 관련 API")
public class MyProfileController {

    private final MyProfileService myProfileService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping
    @Operation(summary = "내 프로필 조회", description = "내 프로필 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MyProfileResponse>> getMyProfile(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);

        MyProfileResponse response = myProfileService.getMyProfile(memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PutMapping
    @Operation(summary = "내 프로필 수정", description = "내 프로필 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> updateMyProfile(
            @RequestBody MyProfileUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        String token = JwtTokenUtil.extractToken(httpRequest);
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);

        myProfileService.updateMyProfile(memberId, request);
        return ResponseEntity.ok(ApiResponse.onSuccess("프로필 수정 완료"));
    }
}
