package com.umc.banddy.domain.member.service;

import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.member.web.dto.LoginRequest;
import com.umc.banddy.domain.member.web.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberLoginService {

    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.AUTHENTICATION_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new GeneralException(ErrorStatus.AUTHENTICATION_FAILED);
        }

        String accessToken = jwtTokenUtil.generateAccessToken(member);
        String refreshToken = jwtTokenUtil.generateRefreshToken(member);

        member.setRefreshToken(refreshToken);
        memberRepository.save(member);

        return new LoginResponse(member.getId(), accessToken, refreshToken);
    }
}
