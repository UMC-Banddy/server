package com.umc.banddy.domain.auth.web.controller;

import com.umc.banddy.domain.auth.web.dto.EmailSendRequest;
import com.umc.banddy.domain.auth.web.dto.EmailVerifyRequest;
import com.umc.banddy.domain.auth.web.dto.EmailVerifyResponse;
import com.umc.banddy.domain.auth.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "email-verification", description = "이메일 인증번호 전송 및 확인 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @Operation(
            summary = "이메일 인증번호 전송",
            description = "사용자의 이메일로 인증번호를 전송합니다. 인증번호는 5분간 유효합니다."
    )
    @PostMapping("/send")
    public ResponseEntity<String> sendCode(@RequestBody @Valid EmailSendRequest request) {
        emailVerificationService.sendCode(request);
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    @Operation(
            summary = "이메일 인증번호 확인"
    )
    @PostMapping("/verify")
    public ResponseEntity<EmailVerifyResponse> verifyCode(@RequestBody @Valid EmailVerifyRequest request) {
        return ResponseEntity.ok(emailVerificationService.verifyCode(request));
    }
}
