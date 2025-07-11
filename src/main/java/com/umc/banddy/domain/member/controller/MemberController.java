package com.umc.banddy.domain.member.controller;

import com.umc.banddy.domain.member.dto.LoginRequest;
import com.umc.banddy.domain.member.dto.LoginResponse;
import com.umc.banddy.domain.member.dto.SignupRequest;
import com.umc.banddy.domain.member.service.MemberCommandService;
import com.umc.banddy.domain.member.service.MemberLoginService;
import com.umc.banddy.domain.member.dto.NicknameCheckResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@Tag(name = "member", description = "회원 관련 API")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberLoginService memberLoginService;

    @Operation(summary = "회원가입", description = "회원가입 api")
    @PostMapping("/join")
    public ResponseEntity<Map<String, String>> signup(@RequestBody @Valid SignupRequest request) {
        memberCommandService.signup(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "회원가입이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 시도합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(memberLoginService.login(request));
    }
    @Operation(summary = "닉네임 중복 체크", description = "닉네임이 사용 가능한지 확인합니다.")
    @GetMapping("/check-nickname")
    public ResponseEntity<NicknameCheckResponse> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(memberCommandService.checkNickname(nickname));
    }

}
