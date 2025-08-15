package com.umc.banddy.domain.auth.service;

import com.umc.banddy.domain.auth.web.dto.EmailSendRequest;
import com.umc.banddy.domain.auth.web.dto.EmailVerifyRequest;
import com.umc.banddy.domain.auth.web.dto.EmailVerifyResponse;
import com.umc.banddy.domain.member.enums.Status;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.handler.AuthHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final Duration CODE_TTL = Duration.ofMinutes(5);

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;

    // 인증번호 전송
    public void sendCode(EmailSendRequest request) {
        final String email = normalize(request.getEmail());

        // 이미 가입된 이메일인지 확인 (ACTIVE + INACTIVE 모두 차단)
        if (memberRepository.existsByEmailAndStatusIn(email, List.of(Status.ACTIVE, Status.INACTIVE))) {
            throw new AuthHandler(ErrorStatus.EMAIL_ALREADY_EXISTS);
        }

        // 새 코드 생성
        final String code = generateCode();

        // Redis 저장 방식 변경: email -> code (덮어쓰기)
        //    재발급 시 기존 코드가 즉시 무효화됨
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(emailKey(email), code, CODE_TTL);

        // 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
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

    // 인증번호 검증
    public EmailVerifyResponse verifyCode(EmailVerifyRequest request) {
        final String email = normalize(request.getEmail());
        final String inputCode = request.getCode();

        String savedCode = redisTemplate.opsForValue().get(emailKey(email));

        // 만료
        if (savedCode == null) {
            throw new AuthHandler(ErrorStatus.VERIFICATION_CODE_EXPIRED);
        }
        // 불일치
        if (!savedCode.equals(inputCode)) {
            throw new AuthHandler(ErrorStatus.VERIFICATION_CODE_WRONG);
        }

        // 인증 성공 → 일회성 사용: 즉시 삭제
        redisTemplate.delete(emailKey(email));

        return new EmailVerifyResponse(true, "인증이 완료되었습니다.");
    }


    private String emailKey(String email) {
        return "email:verify:" + email;
    }

    private String normalize(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(90000) + 10000);  // 10000~99999
    }
}