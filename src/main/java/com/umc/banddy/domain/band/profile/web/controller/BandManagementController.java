package com.umc.banddy.domain.band.profile.web.controller;

import com.umc.banddy.domain.band.profile.service.BandManagementService;
import com.umc.banddy.domain.band.profile.web.dto.Recruitment.*;
import com.umc.banddy.domain.chat.service.ChatRoomService;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BandManagementController {

    private final JwtTokenUtil jwtTokenUtil;
    private final BandManagementService bandManagementService;
    private final ChatRoomService chatRoomService;

    @Operation(summary = "밴드 모집방 만들기")
    @PostMapping(path = "/recruitments")
    public ResponseEntity<RecruitmentResponse> createBand(
            @RequestBody  @Valid RecruitmentRequest recruit,
//            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);

        return ResponseEntity.ok(bandManagementService.createRecruitment(recruit,currentMemberId));
    }

    @Operation(summary = "밴드 모집방 수정하기")
    @PatchMapping(path = "/recruitments")
    public ResponseEntity<RecruitmentResponse> updateBand(
            @RequestBody  @Valid RecruitmentUpdateRequest recruit ,
//            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(bandManagementService.updateRecruitment(recruit,currentMemberId));
    }

    @Operation(summary = "밴드 지원하기")
    @PostMapping("/bands/{bandId}/join")
    public ResponseEntity<BandApplicationResponse> createBandApplication(
            @PathVariable Long bandId,
            @RequestBody @Valid BandApplicationRequest bandApplicationRequest,
            HttpServletRequest request
    ){
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(bandManagementService.createChatRoomForApplication(bandId, currentMemberId,bandApplicationRequest.getSession()));
    }


    @Operation(summary = "밴드 지원자 채팅방 불러오기")
    @GetMapping("/recruitments/{bandId}")
    public ResponseEntity<ApplicationListResponse> getApplicationList(
            @PathVariable Long bandId,
            HttpServletRequest request
    ){
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(bandManagementService.getApplicationList(bandId, currentMemberId));
    }
//    @Operation(summary = "밴드 합격 불합격 처리")
//    @PatchMapping("/recruitments/{bandId}")
//    public ResponseEntity<ApplicationListResponse> updateApplicantStatus(
//            @PathVariable Long bandId,
//            ApplicantUpdateRequest applicantUpdateRequest,
//            HttpServletRequest request
//    ){
//        String token = JwtTokenUtil.extractToken(request);
//        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
//        return ResponseEntity.ok(bandManagementService.updateApplicant(currentMemberId, applicantUpdateRequest, bandId));
//    }

}
