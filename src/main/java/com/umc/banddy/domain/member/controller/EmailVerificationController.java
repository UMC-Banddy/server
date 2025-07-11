package com.umc.banddy.domain.member.controller;

import com.umc.banddy.domain.member.dto.EmailSendRequest;
import com.umc.banddy.domain.member.dto.EmailVerifyRequest;
import com.umc.banddy.domain.member.dto.EmailVerifyResponse;
import com.umc.banddy.domain.member.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestBody @Valid EmailSendRequest request) {
        emailVerificationService.sendCode(request);
        return ResponseEntity.ok("인증번호가 전송되었습니다.");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<EmailVerifyResponse> verifyCode(@RequestBody @Valid EmailVerifyRequest request) {
        return ResponseEntity.ok(emailVerificationService.verifyCode(request));
    }
}
