package com.umc.banddy.domain.mypage.notification.web.controller;

import com.umc.banddy.domain.mypage.notification.service.NotificationService;
import com.umc.banddy.domain.mypage.notification.web.dto.NotificationResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "알림")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Long memberId;
        try {
            memberId = jwtTokenUtil.getMemberIdFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(notificationService.getAllNotifications(memberId));
    }
}

