package com.umc.banddy.domain.mypage.profile.web.controller;

import com.umc.banddy.domain.mypage.profile.service.MyProfileService;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileResponse;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileUpdateRequest;
import com.umc.banddy.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "마이페이지", description = "내 프로필 관련 API")
public class MyProfileController {

    @GetMapping
    @Operation(summary = "내 프로필 조회", description = "내 프로필 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MyProfileResponse>> getMyProfile() {

        // 목업 응답 생성
        MyProfileResponse response = new MyProfileResponse(
                1L,
                "BECK",
                "https://example.com/profile.jpg",
                "나는 파리의 택시운전사",
                List.of("J-POP", "J-Rock", "보컬화이트", "편트렌드팝", "바이브릭", "뉴본"),
                List.of(
                        new MyProfileResponse.SavedTrack("최근 저장곡1", "https://example.com/song1.jpg"),
                        new MyProfileResponse.SavedTrack("최근 저장곡2", "https://example.com/song2.jpg"),
                        new MyProfileResponse.SavedTrack("최근 저장곡3", "https://example.com/song3.jpg")
                ),
                true
        );

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    private final MyProfileService myProfileService;

    @PutMapping
    @Operation(summary = "내 프로필 수정", description = "내 프로필 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> updateMyProfile(@RequestBody MyProfileUpdateRequest request) {
        Long memberId = 1L; // 추후 인증으로 대체 예정
        myProfileService.updateMyProfile(memberId, request);
        return ResponseEntity.ok(ApiResponse.onSuccess("프로필 수정 완료"));
    }

}
