package com.umc.banddy.domain.member.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.member.web.dto.SignupRequest;
import com.umc.banddy.domain.member.web.dto.SignupResponse;
import com.umc.banddy.global.apiPayload.exception.handler.AuthHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import com.umc.banddy.domain.member.web.dto.NicknameCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import com.umc.banddy.domain.member.enums.Status;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.domain.member.enums.Role;

@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new AuthHandler(ErrorStatus.EMAIL_ALREADY_EXISTS);
        }

        if (request.getAge() < 0) {
            throw new AuthHandler(ErrorStatus.INVALID_AGE);
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .age(request.getAge())
                .gender(request.getGender())
                .region(request.getRegion())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        return memberRepository.save(member);
    }

    public NicknameCheckResponse checkNickname(String nickname) {
        boolean exists = memberRepository.existsByNickname(nickname);
        if (exists) {
            return new NicknameCheckResponse(false, "이미 존재하는 닉네임입니다.");
        } else {
            return new NicknameCheckResponse(true, "사용 가능한 닉네임입니다.");
        }
    }
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정
    @Transactional
    public void deleteInactiveMembers() {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        List<Member> membersToDelete = memberRepository.findByStatusAndInactiveDateBefore(Status.INACTIVE, sevenDaysAgo);
        memberRepository.deleteAll(membersToDelete);
    }
}
