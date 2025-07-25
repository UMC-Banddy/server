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


    //  친구 목록 조회
    @Operation(summary = "친구 목록 조회")
    @GetMapping
    public ResponseEntity<List<FriendResponseDto>> getFriendList(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);

        List<FriendResponseDto> friends = friendService.getMyFriends(memberId);
        return ResponseEntity.ok(friends);
    }

    //  친구 삭제
    @Operation(summary = "친구 삭제", description = "친구를 삭제합니다")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long friendId) {
        friendService.deleteFriend(friendId);
        return ResponseEntity.ok().build();
    }
}
