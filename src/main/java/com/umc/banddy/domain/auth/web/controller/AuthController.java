package com.umc.banddy.domain.auth.web.controller;

import com.umc.banddy.domain.auth.service.AuthService;
import com.umc.banddy.domain.auth.web.dto.LogoutRequest;
import com.umc.banddy.domain.auth.web.dto.DeactivateRequest;
import com.umc.banddy.domain.auth.web.dto.RefreshTokenRequest;
import com.umc.banddy.global.apiPayload.ApiResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.HashMap;
import java.util.Map;

@Tag(name = "auth", description = "인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그아웃", description = "로그아웃을 합니다")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid LogoutRequest logoutRequest
    ) {

        authService.logout(authorization, logoutRequest.getRefreshToken());

        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "액세스 토큰 재발급", description = "유효한 리프레시 토큰으로 액세스 토큰을 재발급합니다.")
    @PostMapping("/refreshToken")
    public ResponseEntity<Map<String, String>> reissueAccessToken(
            @RequestBody @Valid RefreshTokenRequest request) {

        String newAccessToken = authService.reissueAccessToken(request.getRefreshToken());

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "회원 탈퇴", description = "회원탈퇴 7일후 회원정보가 완전히 삭제됩니다.")
    @PatchMapping("/inactive")
    public ApiResponse<String> deactivate(@RequestBody DeactivateRequest request) {
        authService.deactivateMember(request.getMemberId(), request.getRefreshToken());
        return ApiResponse.onSuccess("회원 탈퇴가 완료되었습니다.");
    }
}