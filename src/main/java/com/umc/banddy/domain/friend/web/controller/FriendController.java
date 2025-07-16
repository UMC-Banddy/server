package com.umc.banddy.domain.friend.web.controller;

import com.umc.banddy.domain.friend.web.dto.FriendRequestDto;
import com.umc.banddy.domain.friend.web.dto.FriendResponseDto;
import com.umc.banddy.domain.friend.service.FriendService;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "friend", description = "친구 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendService friendService;
    private final JwtTokenUtil jwtTokenUtil;

    //  친구 신청
    @Operation(summary = "친구 신청", description = "친구 신청 시 사용합니다")
    @PostMapping("/request")
    public ResponseEntity<Void> requestFriend(@RequestBody FriendRequestDto dto,
                                              HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);

        friendService.requestFriend(memberId, dto.getTargetMemberId());
        return ResponseEntity.ok().build();
    }

    //  친구 수락
    @Operation(summary = "친구신청 수락", description = "친구 신청을 수락합니다")
    @PostMapping("/accept/{friendId}")
    public ResponseEntity<Void> acceptFriend(@PathVariable Long friendId) {
        friendService.acceptFriend(friendId);
        return ResponseEntity.ok().build();
    }

    //  친구 거절
    @Operation(summary = "친구신청 거절", description = "친구 신청을 거절합니다")
    @PostMapping("/reject/{friendId}")
    public ResponseEntity<Void> rejectFriend(@PathVariable Long friendId) {
        friendService.rejectFriend(friendId);
        return ResponseEntity.ok().build();
    }

    //  친구 목록 조회
    @Operation(summary = "친구 목록 조회")
    @GetMapping
    public ResponseEntity<List<FriendResponseDto>> getFriendList(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);

        List<FriendResponseDto> friends = friendService.getMyFriends(memberId);
        return ResponseEntity.ok(friends);
    }

    //  받은 신청 목록 조회
    @Operation(summary = "친구 신청 목록 조회", description = "받은 친구 신청을 조회합니다")
    @GetMapping("/requests")
    public ResponseEntity<List<FriendResponseDto>> getRequests(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);

        List<FriendResponseDto> requests = friendService.getReceivedFriendRequests(memberId);
        return ResponseEntity.ok(requests);
    }

    //  요청 상세 조회 (알림 클릭 시)
    @Operation(summary = "친구 신청 상세 조회", description = "받은 친구 신청을 상세 조회합니다")
    @GetMapping("/requests/{friendId}")
    public ResponseEntity<FriendResponseDto> getRequestDetail(@PathVariable Long friendId) {
        return ResponseEntity.ok(friendService.getFriendRequestDetail(friendId));
    }

    //  친구 삭제
    @Operation(summary = "친구 삭제", description = "친구를 삭제합니다")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long friendId) {
        friendService.deleteFriend(friendId);
        return ResponseEntity.ok().build();
    }
}
