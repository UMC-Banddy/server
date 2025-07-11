package com.umc.banddy.global.security.jwt;

import io.jsonwebtoken.*;
import com.umc.banddy.domain.member.domain.Member;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${spring.jwt.secret-key}")
    private String secret;

    private static final long ACCESS_EXPIRATION = 1000 * 60 * 30; // 30분
    private static final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7일

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    //  Access Token 생성
    public String generateAccessToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getEmail())
                .setId(member.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    //  Refresh Token 생성
    public String generateRefreshToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getEmail())
                .setId(member.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    //  이메일 추출
    public String getEmailFromToken(String token) {
        return parseToken(token).getSubject();
    }

    //  memberId 추출
    public Long getMemberIdFromToken(String token) {
        return Long.parseLong(parseToken(token).getId());
    }

    //  유효성 검사
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 내부 파서
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return (authorization != null && authorization.startsWith("Bearer "))
                ? authorization.substring(7)
                : null;
    }
    public LocalDateTime getExpirationDate(String token) {
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}

