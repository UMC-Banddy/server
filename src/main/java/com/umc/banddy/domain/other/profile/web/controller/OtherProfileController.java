package com.umc.banddy.domain.other.profile.web.controller;

import com.umc.banddy.domain.other.profile.service.OtherProfileService;
import com.umc.banddy.domain.other.profile.web.dto.OtherProfileResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class OtherProfileController {

    private final OtherProfileService otherProfileService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/{memberId}/profile")
    public ResponseEntity<OtherProfileResponse> getOtherProfile(
            @PathVariable("memberId") Long targetMemberId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Long loginMemberId;
        try {
            loginMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        OtherProfileResponse response = otherProfileService.getOtherProfile(loginMemberId, targetMemberId);
        return ResponseEntity.ok(response);
    }

}
