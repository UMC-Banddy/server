package com.umc.banddy.domain.band.profile.web.controller;

import com.umc.banddy.domain.band.profile.service.BandManagementService;
import com.umc.banddy.domain.band.profile.web.dto.Recruitment.RecruitmentRequest;
import com.umc.banddy.domain.band.profile.web.dto.Recruitment.RecruitmentResponse;
import com.umc.banddy.domain.band.profile.web.dto.Recruitment.RecruitmentUpdateRequest;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
public class BandManagementController {

    private final JwtTokenUtil jwtTokenUtil;
    private final BandManagementService bandManagementService;

    @Operation(summary = "밴드 모집방 만들기")
    @PostMapping("/")
    public ResponseEntity<RecruitmentResponse> createBand(@RequestBody @Valid RecruitmentRequest recruit , HttpServletRequest request) {
        return ResponseEntity.ok(bandManagementService.createRecruitment(recruit));
    }

    @Operation(summary = "밴드 모집방 수정하기")
    @PatchMapping("/")
    public ResponseEntity<RecruitmentResponse> updateBand(@RequestBody @Valid RecruitmentUpdateRequest recruit , HttpServletRequest request) {
        return ResponseEntity.ok(bandManagementService.updateRecruitment(recruit));
    }

}
