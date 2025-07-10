package com.umc.banddy.domain.member.controller;

import com.umc.banddy.domain.member.dto.LoginRequest;
import com.umc.banddy.domain.member.dto.LoginResponse;
import com.umc.banddy.domain.member.dto.SignupRequest;
import com.umc.banddy.domain.member.service.MemberCommandService;
import com.umc.banddy.domain.member.service.MemberLoginService;
import com.umc.banddy.domain.member.dto.NicknameCheckResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberLoginService memberLoginService;

    @PostMapping("/join")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        memberCommandService.signup(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(memberLoginService.login(request));
    }
    @GetMapping("/check-nickname")
    public ResponseEntity<NicknameCheckResponse> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(memberCommandService.checkNickname(nickname));
    }

}
