package com.umc.banddy.domain.auth.service;

import com.umc.banddy.domain.auth.domain.TokenBlackList;
import com.umc.banddy.domain.auth.repository.TokenBlacklistRepository;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public void addToBlackList(String refreshToken) {
        LocalDateTime expiredAt = jwtTokenUtil.getExpirationDate(refreshToken);
        TokenBlackList tokenBlackList = TokenBlackList.builder()
                .token(refreshToken)
                .expiredAt(expiredAt)
                .build();
        tokenBlacklistRepository.save(tokenBlackList);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    @Transactional
    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시에 만료 토큰 삭제
    public void deleteExpiredTokens() {
        tokenBlacklistRepository.deleteAllByExpiredAtBefore(LocalDateTime.now());
    }
}
