package com.umc.banddy.domain.chat.web.controller;

import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.service.ChatMessageService;
import com.umc.banddy.domain.chat.service.ChatRoomService;
import com.umc.banddy.domain.chat.service.ChatService;
import com.umc.banddy.domain.chat.web.dto.ChatRoom.*;
import com.umc.banddy.domain.chat.web.dto.Message.ChatSystemResponse;
import com.umc.banddy.domain.chat.web.dto.Message.CursorChatMessageResponse;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat")
public class ChatContoller {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ChatMessageService chatMessageService;


    @Operation(summary = " 단체 채팅방 생성", description = "단체 채팅방 생성 api")
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @RequestBody @Valid ChatRoomRequest chatRoomRequest,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(chatRoomService.createGroupChatRoom(currentMemberId, chatRoomRequest));
    }

    @Operation(summary = "개인 채팅방 조회")
    @PostMapping("/rooms/friends")
    public ResponseEntity<PrivateChatRoomResponse> createPrivateChatRooms(
            @RequestBody @Valid PrivateChatRoomRequest privateChatRoomRequest,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(chatRoomService.getPrivateChatRoom(currentMemberId, privateChatRoomRequest.getMemberId()));
    }

    @Operation(summary="채팅 참여")
    @PostMapping("/rooms/{roomId}/members/join")
    public ResponseEntity<ChatSystemResponse> joinChatRoom(
            @PathVariable Long roomId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        Pair<ChatRoom, Member> pair = chatService.verifedChatRoomAndMember(roomId, currentMemberId);
        return ResponseEntity.ok(chatMessageService.joinChatRoom(pair.getLeft(), pair.getRight()));
    }

    @Operation(summary = "채팅방 나가기")
    @PostMapping("/rooms/{roomId}/members/exit")
    public ResponseEntity<ChatSystemResponse> exitChatRoom(
            @PathVariable Long roomId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        Pair<ChatRoom,Member> pair = chatService.verifedChatRoomAndMember(roomId,currentMemberId);
        return ResponseEntity.ok(chatMessageService.exitChatRoom(pair.getLeft(), pair.getRight()));
    }

    @Operation(summary = "메세지 무한 스크롤")
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<CursorChatMessageResponse> getChatMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false, defaultValue = "0") Long cursor,
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(chatMessageService.getChatMessages(roomId, cursor, limit));
    }

    @Operation(summary = "채팅방 목록 조회")
    @GetMapping("/rooms")
    public ResponseEntity <ChatRoomListResponse> getChatRooms(
            HttpServletRequest request
    ){
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(chatRoomService.getMyChatRooms(currentMemberId));
    }

    @Operation(summary = "친구 채팅방 목록 조회")
    @GetMapping("/friends")
    public ResponseEntity <FriendsChatRoomResponse> getFriendsChatRooms(
            HttpServletRequest request
    ){
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(chatRoomService.getFriendsChatRoom(currentMemberId));
    }

    @Operation(summary = "채팅방 참가자 정보 불러오기")
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity <ParticipantInfos> getChatRoomInfo(
            @PathVariable Long roomId,
            HttpServletRequest request
    ){
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        Pair<ChatRoom,Member> pair = chatService.verifedChatRoomAndMember(roomId, currentMemberId);
        return ResponseEntity.ok(chatRoomService.getChatRoomInfo(pair.getLeft(),pair.getRight()));
    }

    @Operation(summary = "밴드 지원하기")
    @PostMapping("/bands/{bandId}/join")
    public ResponseEntity<GroupChatRoomResponse> joinBand(
            @PathVariable Long bandId,
            @RequestBody @Valid BandJoinRequest bandJoinRequest,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(chatRoomService.joinBand(bandId, currentMemberId, bandJoinRequest.getSession()));
    }
}
