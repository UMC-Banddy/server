package com.umc.banddy.domain.band.profile.web.controller;

import com.umc.banddy.domain.band.profile.service.BandManagementService;
import com.umc.banddy.domain.chat.web.dto.ChatRoomRequest;
import com.umc.banddy.domain.chat.web.dto.ChatRoomResponse;
import com.umc.banddy.domain.chat.web.dto.RecruitmentRequest;
import com.umc.banddy.domain.chat.web.dto.RecruitmentResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
public class BandManagementController {

    private JwtTokenUtil jwtTokenUtil;
    private BandManagementService bandManagementService;

    @Operation(summary = "밴드 모집방 만들기")
    @PostMapping("/")
    public ResponseEntity<RecruitmentResponse> createChatRoom(@RequestBody @Valid RecruitmentRequest recruit , HttpServletRequest request) {

        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(bandManagementService.createRecruitment(recruit));
    }


}
