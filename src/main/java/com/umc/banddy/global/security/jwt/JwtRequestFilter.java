package com.umc.banddy.global.security.jwt;

import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.handler.AuthHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Authorization 헤더가 존재하고 Bearer로 시작하는 경우
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                //  토큰 유효성 검사 (유효하지 않으면 예외 발생)
                if (jwtTokenUtil.validateToken(token)) {
                    String email = jwtTokenUtil.getEmailFromToken(token);

                    // 인증 객체 등록
                    User principal = new User(email, "", Collections.emptyList());
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new AuthHandler(ErrorStatus.INVALID_TOKEN); // 유효하지 않으면 에러 던짐
                }

            } catch (Exception e) {
                throw new AuthHandler(ErrorStatus.INVALID_TOKEN); // 파싱 중 예외도 동일하게 처리
            }
        }

        // 4. 토큰이 없거나 정상 인증된 경우 다음 필터 진행
        chain.doFilter(request, response);
    }
}
