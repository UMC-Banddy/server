package com.umc.banddy.domain.member.service;

import com.umc.banddy.domain.member.Member;
import com.umc.banddy.domain.member.MemberRepository;
import com.umc.banddy.domain.member.dto.SignupRequest;
import com.umc.banddy.domain.member.dto.NicknameCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .age(request.getAge())
                .gender(request.getGender())
                .region(request.getRegion())
                .district(request.getDistrict())
                .build();

        memberRepository.save(member);
    }
    public NicknameCheckResponse checkNickname(String nickname) {
        boolean exists = memberRepository.existsByNickname(nickname);
        if (exists) {
            return new NicknameCheckResponse(false, "이미 존재하는 닉네임입니다.");
        } else {
            return new NicknameCheckResponse(true, "사용 가능한 닉네임입니다.");
        }
    }
}
