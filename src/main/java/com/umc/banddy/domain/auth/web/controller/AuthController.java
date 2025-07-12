package com.umc.banddy.domain.auth.web.controller;

import com.umc.banddy.domain.auth.service.AuthService;
import com.umc.banddy.domain.auth.web.dto.LogoutRequest;
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
        String accessToken = authorization.startsWith("Bearer ") ?
                authorization.substring(7) : authorization;

        authService.logout(accessToken, logoutRequest.getRefreshToken());

        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

}