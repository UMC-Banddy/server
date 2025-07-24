package com.umc.banddy.domain.chat.web.controller;

import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.service.ChatService;
import com.umc.banddy.domain.chat.web.dto.*;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat")
public class ChatContoller {

    private final ChatService chatService;
    private final JwtTokenUtil jwtTokenUtil;

    @Operation(summary = " 단체 채팅방 생성", description = "단체 채팅방 생성 api")
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @RequestBody @Valid ChatRoomRequest chatRoomRequest,
            HttpServletRequest request
            ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(chatService.createGroupChatRoom(currentMemberId, chatRoomRequest));
    }

    @Operation(summary = "개인 채팅방 생성", description = "개인 채팅방 생성 api")
    @PostMapping("/rooms/friends")
    public ResponseEntity<PrivateChatRoomResponse> createPrivateChatRooms(
            Principal principal,
            PrivateChatRoomRequest request
    ) {
        return ResponseEntity.ok(chatService.createPrivateChatRoom(principal,request));
    }

    @Operation(summary="채팅 참여")
    @PostMapping("/rooms/{roomId}/members/join")
    public ResponseEntity<ChatSystemResponse> joinChatRoom(
            HttpServletRequest request,
            @PathVariable Long roomId
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        Pair<ChatRoom, Member> pair = chatService.verifedRoomAndMember(roomId, currentMemberId);
        return ResponseEntity.ok(chatService.joinChatRoom(pair.getLeft(), pair.getRight()));

    }



    //    @Operation(summary = "밴드/그룹 채팅방 조회", description = "밴드 또는 그룹 채팅방 조회 api")
//    @GetMapping("/rooms")
//    public ResponseEntity<> getChatRooms(){
//
//    }
}
