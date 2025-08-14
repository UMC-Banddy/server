package com.umc.banddy.domain.band.profile.web.controller;

import com.umc.banddy.domain.band.profile.service.BandManagementService;
import com.umc.banddy.domain.band.profile.web.dto.Recruitment.*;
import com.umc.banddy.domain.chat.service.ChatRoomService;
import com.umc.banddy.domain.chat.web.dto.chatroom.BasicChatRoomInfo;
import com.umc.banddy.global.apiPayload.ApiResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BandManagementController {

    private final JwtTokenUtil jwtTokenUtil;
    private final BandManagementService bandManagementService;
    private final ChatRoomService chatRoomService;


    @Operation(summary = "밴드 모집방 만들기" ,description = """
    - 세션 타입 "🎤 보컬 🎤" , "🎸 일렉 기타 " , "🪕 어쿠스틱 기타 🪕" ,"🎵 베이스 🎵" , "🥁 드럼 🥁" , "🎹 키보드 🎹" , "🎻 바이올린 🎻" , "🎺 트럼펫 🎺"
        - session은 모집할 세션, currnetSession 밴드에 속한 멤버의 세션을 의미
    - 장르 타입 "Metal", "New age", "Pop", "Punk", "R&B", "Rock", "Grunge", "Indie Rock", "Jazz", "Shoegaze", "EMO", "Psychedelia", "Dream Pop", "Nu Metal", "J-pop", "Tiwan Indie"
    - 모집방 상태 타입 "RECRUITING","ACTIVE","ENDED"
    - 성별 남성 - "MALE", 여성 - "FEMALE", 성별무관 - "OTHER"
    ---
    - trackSpotifyIds 예시
        - 안녕- 5rNyAQzncPBVdEgEG4okNK,
        - 폭죽과 풍선들- 3P3guXf2RRhjPK0R2UlLZV
    - artistSpotifyIds 예시
        - 우효 - 50Zu2bK9y5UAtD0jcqk5VX,
        - 검정치마- 6WeDO4GynFmK4OxwkBzMW8
  """)
    @PostMapping(path = "/recruitments", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<RecruitmentResponse>> createBand(
            @RequestPart(value = "data")  @Valid RecruitmentRequest recruit,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);

        return ResponseEntity.ok(ApiResponse.onSuccess(bandManagementService.createRecruitment(recruit,image,currentMemberId)));
    }


    @Operation(summary = "밴드 모집방 수정하기",description = """
    - 세션 타입 "🎤 보컬 🎤" , "🎸 일렉 기타 " , "🪕 어쿠스틱 기타 🪕" ,"🎵 베이스 🎵" , "🥁 드럼 🥁" , "🎹 키보드 🎹" , "🎻 바이올린 🎻" , "🎺 트럼펫 🎺"
        - session은 모집할 세션, currnetSession 밴드에 속한 멤버의 세션을 의미
    - 장르 타입 "Metal", "New age", "Pop", "Punk", "R&B", "Rock", "Grunge", "Indie Rock", "Jazz", "Shoegaze", "EMO", "Psychedelia", "Dream Pop", "Nu Metal", "J-pop", "Tiwan Indie"
    - 모집방 상태 타입 "RECRUITING","ACTIVE","ENDED"
    - 성별 남성 - "MALE", 여성 - "FEMALE", 성별무관 - "OTHER"
    ---
    - trackSpotifyIds 예시
        - 안녕- 5rNyAQzncPBVdEgEG4okNK
        - 폭죽과 풍선들- 3P3guXf2RRhjPK0R2UlLZV
    - artistSpotifyIds 예시
        - 우효 - 50Zu2bK9y5UAtD0jcqk5VX
        - 검정치마- 6WeDO4GynFmK4OxwkBzMW8
    - 주의! List 형태의 정보는 원래 있던 정보와 비교하여 입력되지 않은 정보는 삭제됩니다
        - A, B, C가 있는 상태에서 A만 입력했다면 수정후 A만 남고 B, C는 삭제
  """)
    @PatchMapping(path = "/recruitments", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<RecruitmentResponse>> updateBand(
            @RequestPart(value = "data")                RecruitmentUpdateRequest recruit ,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(bandManagementService.updateRecruitment(recruit,image, currentMemberId)));
    }


    @Operation(summary = "밴드 모집방 정보 불러오기")
    @GetMapping(path = "/recruitments/{bandId}")
    public ResponseEntity<ApiResponse<BandInquiryResponse>> getBand(
            @NotNull @Positive @PathVariable Long bandId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(bandManagementService.getRecruitment(currentMemberId,bandId)));
    }


    @Operation(summary = "밴드 지원하기",description = """
    - ** 세션 타입 "🎤 보컬 🎤" , "🎸 일렉 기타 " , "🪕 어쿠스틱 기타 🪕" ,"🎵 베이스 🎵" , "🥁 드럼 🥁" , "🎹 키보드 🎹" , "🎻 바이올린 🎻" , "🎺 트럼펫 🎺"
    """)
    @PostMapping("/bands/{bandId}/join")
    public ResponseEntity<ApiResponse<BasicChatRoomInfo>> createBandApplication(
            @NotNull @Positive @PathVariable Long bandId,
            @RequestBody @Valid BandApplicationRequest bandApplicationRequest,
            HttpServletRequest request
    ){
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(bandManagementService.createChatRoomForApplication(bandId, currentMemberId,bandApplicationRequest.getSession())));
    }


    @Operation(summary = "밴드 지원자 채팅방 불러오기")
    @GetMapping("/recruitments/{bandId}/applications")
    public ResponseEntity<ApiResponse<ApplicationListResponse>> getApplicationList(
            @NotNull @Positive @PathVariable Long bandId,
            HttpServletRequest request
    ){
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(bandManagementService.getApplicationList(bandId, currentMemberId)));
    }

    @Operation(summary = "밴드 합격 불합격 처리",description = """
     - Status "PASS", "FAIL"
    """)
    @PatchMapping("/recruitments/{bandId}")
    public ResponseEntity<ApiResponse<ApplicationListResponse>> updateApplicantStatus(
            @NotNull @Positive @PathVariable Long bandId,
            @RequestBody ApplicantUpdateRequest applicantUpdateRequest,
            HttpServletRequest request
    ){
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(ApiResponse.onSuccess(bandManagementService.updateApplicant(currentMemberId, applicantUpdateRequest, bandId)));
    }

}
