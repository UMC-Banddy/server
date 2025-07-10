package com.umc.banddy.domain.member.service;

import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import com.umc.banddy.domain.member.Member;
import com.umc.banddy.domain.member.MemberRepository;
import com.umc.banddy.domain.member.dto.LoginRequest;
import com.umc.banddy.domain.member.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MemberLoginService {

    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenUtil.generateAccessToken(member);
        String refreshToken = jwtTokenUtil.generateRefreshToken(member);

        member.setRefreshToken(refreshToken);
        memberRepository.save(member);

        return new LoginResponse(member.getId(), accessToken, refreshToken);
    }

}
