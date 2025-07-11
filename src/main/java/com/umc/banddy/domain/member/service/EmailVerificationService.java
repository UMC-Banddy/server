package com.umc.banddy.domain.member.service;

import com.umc.banddy.domain.member.dto.EmailSendRequest;
import com.umc.banddy.domain.member.dto.EmailVerifyRequest;
import com.umc.banddy.domain.member.dto.EmailVerifyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;

    private final Map<String, String> verificationStore = new HashMap<>();

    public void sendCode(EmailSendRequest request) {
        String code = generateCode();
        verificationStore.put(request.getEmail(), code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("[Banddy] 이메일 인증번호");
        message.setText("인증번호는 [" + code + "] 입니다.\n5분 이내에 입력해주세요.");

        mailSender.send(message);
    }

    public EmailVerifyResponse verifyCode(EmailVerifyRequest request) {
        String storedCode = verificationStore.get(request.getEmail());
        if (storedCode != null && storedCode.equals(request.getCode())) {
            verificationStore.remove(request.getEmail()); // 인증 성공 시 삭제
            return new EmailVerifyResponse(true, "인증이 완료되었습니다.");
        }
        return new EmailVerifyResponse(false, "인증번호가 일치하지 않습니다.");
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}
