package com.umc.banddy.domain.mypage.notification.web.controller;

import com.umc.banddy.domain.mypage.notification.enums.NotificationType;
import com.umc.banddy.domain.mypage.notification.service.NotificationService;
import com.umc.banddy.domain.mypage.notification.web.dto.NotificationResponse;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "알림", description = "사용자 알림 조회 API")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(HttpServletRequest request) {
        Long memberId = extractMemberIdOrThrow(request);
        List<NotificationResponse> notifications = notificationService.getAllNotifications(memberId);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/read")
    public ResponseEntity<Void> markAsRead(
            @RequestParam("type") NotificationType type,
            @RequestParam("notificationId") Long notificationId
    ) {
        validateIdOrThrow(notificationId);
        notificationService.markNotificationAsRead(type, notificationId);
        return ResponseEntity.ok().build();
    }

    private Long extractMemberIdOrThrow(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        if (token == null || token.isBlank()) throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        try {
            return jwtTokenUtil.getMemberIdFromToken(token);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }
    }

    private void validateIdOrThrow(Long id) {
        if (id == null || id <= 0) throw new GeneralException(ErrorStatus._BAD_REQUEST);
    }
}
