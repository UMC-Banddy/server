package com.umc.banddy.domain.auth.service;

import com.umc.banddy.domain.auth.service.TokenBlacklistService;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.handler.AuthHandler;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenBlacklistService tokenBlacklistService;
    private final JwtTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        // Refresh Token이 이미 블랙리스트에 있는지 확인
        if (tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
            throw new AuthHandler(ErrorStatus.LOGOUT_TOKEN);
        }

        // Access Token 유효성 검사
        if (!jwtTokenUtil.validateToken(accessToken)) {
            throw new AuthHandler(ErrorStatus.INVALID_ACCESS_TOKEN);
        }

        // Refresh Token 유효성 검사
        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw new AuthHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        // 블랙리스트에 등록
        tokenBlacklistService.addToBlackList(refreshToken);
    }

    @Transactional
    public String reissueAccessToken(String refreshToken) {
        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw new AuthHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        if (tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
            throw new AuthHandler(ErrorStatus.LOGOUT_TOKEN);
        }

        String email = jwtTokenUtil.getEmailFromToken(refreshToken);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AuthHandler(ErrorStatus.MEMBER_NOT_FOUND));

        return jwtTokenUtil.generateAccessToken(member);
    }

}

