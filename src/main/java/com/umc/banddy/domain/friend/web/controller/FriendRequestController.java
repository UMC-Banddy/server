package com.umc.banddy.domain.friend.web.controller;

import com.umc.banddy.domain.friend.web.dto.FriendRequestDto;
import com.umc.banddy.domain.friend.web.dto.FriendRequestResponseDto;
import com.umc.banddy.domain.friend.service.FriendRequestService;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "친구 요청", description = "친구 요청 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend/request")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final JwtTokenUtil jwtTokenUtil;

    // 친구 신청
    @Operation(summary = "친구 신청", description = "특정 유저에게 친구 신청을 보냅니다.")
    @PostMapping
    public ResponseEntity<Void> requestFriend(@RequestBody FriendRequestDto dto,
                                              HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);

        friendRequestService.requestFriend(memberId, dto.getTargetMemberId());
        return ResponseEntity.ok().build();
    }

    // 친구 수락
    @Operation(summary = "친구 신청 수락", description = "받은 친구 신청을 수락합니다.")
    @PostMapping("/{requestId}/accept")
    public ResponseEntity<Void> acceptFriend(@PathVariable Long requestId) {
        friendRequestService.acceptFriend(requestId);
        return ResponseEntity.ok().build();
    }

    // 친구 거절
    @Operation(summary = "친구 신청 거절", description = "받은 친구 신청을 거절합니다.")
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectFriend(@PathVariable Long requestId) {
        friendRequestService.rejectFriend(requestId);
        return ResponseEntity.ok().build();
    }

    // 받은 친구 신청 목록 조회
    /* @Operation(summary = "받은 친구 신청 목록 조회", description = "내가 받은 친구 신청 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<FriendRequestResponseDto>> getReceivedFriendRequests(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);

        return ResponseEntity.ok(friendRequestService.getReceivedFriendRequests(memberId));
    } */

    // 친구 신청 상세 조회
    @Operation(summary = "친구 신청 상세 조회", description = "알림 탭에서 특정 친구 요청의 상세 정보를 조회합니다.")
    @GetMapping("/{requestId}")
    public ResponseEntity<FriendRequestResponseDto> getFriendRequestDetail(@PathVariable Long requestId) {
        return ResponseEntity.ok(friendRequestService.getFriendRequestDetail(requestId));
    }
}
