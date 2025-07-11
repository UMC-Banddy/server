package com.umc.banddy.domain.member.service;

import com.umc.banddy.domain.member.dto.EmailSendRequest;
import com.umc.banddy.domain.member.dto.EmailVerifyRequest;
import com.umc.banddy.domain.member.dto.EmailVerifyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;

    // 기존 Map<String, String> → 인증번호 + 생성시간 저장용 객체로 변경
    private final Map<String, VerificationData> verificationStore = new ConcurrentHashMap<>();

    public void sendCode(EmailSendRequest request) {
        String code = generateCode();

        // key = code (이메일 없이 인증번호로만 저장) 기존 코드에서 변경..
        verificationStore.put(code, new VerificationData(code));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("[Banddy] 회원가입 인증번호입니다.");
        message.setText(
                "안녕하세요, 밴디입니다.\n" +
                        "요청하신 이메일 인증을 위해 아래 인증번호를 입력해 주세요.\n" +
                        "인증번호: " + code + "\n" +
                        "이 인증번호는 5분간 유효합니다.\n" +
                        "감사합니다."
        );

        mailSender.send(message);
    }

    public EmailVerifyResponse verifyCode(EmailVerifyRequest request) {
        // 이메일이 아닌 인증번호로 찾음
        VerificationData data = verificationStore.get(request.getCode());

        if (data == null || data.isExpired()) {
            return new EmailVerifyResponse(false, "인증번호가 만료되었거나 존재하지 않습니다.");
        }

        verificationStore.remove(request.getCode()); // 인증 성공 시 제거
        return new EmailVerifyResponse(true, "인증이 완료되었습니다.");
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(90000) + 10000);
    }

    private static class VerificationData {
        private final String code;
        private final LocalDateTime createdAt;

        public VerificationData(String code) {
            this.code = code;
            this.createdAt = LocalDateTime.now();
        }

        public String getCode() {
            return code;
        }

        public boolean isExpired() {
            return createdAt.plusMinutes(5).isBefore(LocalDateTime.now());
        }
    }
}