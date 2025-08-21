package com.umc.banddy.domain.mypage.profile.web.controller;

import com.umc.banddy.domain.mypage.profile.service.MyProfileService;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileResponse;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileUpdateRequest;
import com.umc.banddy.global.apiPayload.ApiResponse;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        if (token == null || token.isBlank()) throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        try { jwtTokenUtil.getMemberIdFromToken(token); } catch (Exception e) { throw new GeneralException(ErrorStatus._UNAUTHORIZED); }
        MyProfileResponse response = myProfileService.getMyProfile(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "내 프로필 수정", description = "내 프로필 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<MyProfileResponse>> updateMyProfile(
            HttpServletRequest request,
            @RequestPart("data") MyProfileUpdateRequest dto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        MyProfileResponse updated = myProfileService.updateMyProfile(request, dto, profileImage);
        return ResponseEntity.ok(ApiResponse.onSuccess(updated)); // ✅ onSuccess 사용
    }
}
