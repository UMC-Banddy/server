package com.umc.banddy.domain.chat.web.controller;

import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.service.ChatMessageService;
import com.umc.banddy.domain.chat.service.ChatRoomService;
import com.umc.banddy.domain.chat.service.ChatService;
import com.umc.banddy.domain.chat.web.dto.chatroom.*;
import com.umc.banddy.domain.chat.web.dto.chatroom.creation.*;
import com.umc.banddy.domain.chat.web.dto.chatroom.roomlist.ChatRoomListResponse;
import com.umc.banddy.domain.chat.web.dto.message.ChatSystemResponse;
import com.umc.banddy.domain.chat.web.dto.message.CursorChatMessageResponse;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.global.apiPayload.ApiResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat")
public class ChatContoller {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ChatMessageService chatMessageService;

    @Operation(summary = " 단체 채팅방 생성", description = "단체 채팅방 생성 api, 본인을 제외한 참여자 Id 입력")
    @PostMapping(path = "/rooms", consumes = "multipart/form-data")
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @RequestPart(value = "data") @Valid  ChatRoomRequest chatRoomRequest,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(chatRoomService.createGroupChatRoom(currentMemberId, image, chatRoomRequest));
    }
    @Operation(summary = " 단체 채팅방 정보 수정", description = "단체 채팅방 정보 수정 api")
    @PatchMapping(path = "/rooms", consumes = "multipart/form-data")
    public ResponseEntity<UpdateGroupChatResponse> updateChatRoom(
            @RequestPart(value = "data")@Valid UpdateGroupChatRequest updateGroupChatRequest,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(chatRoomService.updateGroupChatRoom(currentMemberId, image, updateGroupChatRequest));
    }

    @Operation(summary = "개인 채팅방 생성, 입장", description = "친구페이지에서 개인 채팅방 입장, 채팅방이 없는 경우도 자동 생성 api")
    @PostMapping("/rooms/friends")
    public ResponseEntity<BasicChatRoomInfo> createPrivateChatRooms(
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
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            HttpServletRequest request
    ) {
        // 초기 요청인 경우(Long.MAX_VALUE = 9_223_372_036_854_775_807)
        long effectiveCursor = (cursor == null) ? Long.MAX_VALUE : cursor;

        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(chatMessageService.getChatMessages(roomId, effectiveCursor, limit));
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

//    @Operation(summary = "친구 채팅방 목록 조회")
//    @GetMapping("/friends")
//    public ResponseEntity <FriendsChatRoomResponse> getFriendsChatRooms(
//            HttpServletRequest request
//    ){
//        String token = JwtTokenUtil.extractToken(request);
//        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
//        return ResponseEntity.ok(chatRoomService.getFriendsChatRoom(currentMemberId));
//    }

    @Operation(summary = "채팅방 입장시 필요 정보 불러오기")
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity <BasicChatRoomInfo> getChatRoomInfo(
            @PathVariable Long roomId,
            HttpServletRequest request
    ){
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        return ResponseEntity.ok(chatRoomService.getChatRoomInfo(roomId, currentMemberId));
    }

    @Operation(summary = "채팅방 고정하기")
    @PatchMapping("/rooms/pin")
    public ResponseEntity <?> pinChatRoom(
            @RequestParam(required = false) Long bandId,
            @RequestParam(required = false) Long chatId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        if( bandId == null && chatId != null) {
           return ResponseEntity.ok(chatRoomService.pinChatRoom( chatId, currentMemberId));
        }else if(bandId != null && chatId == null) {
            return ResponseEntity.ok(chatRoomService.pinBandChatRoom(bandId,currentMemberId));
        }else{
            return ResponseEntity.badRequest().body("bandId or chatId must be provided");
        }
    }
    @Operation(summary = "채팅방 고정 해제",description = " bandId와 chatId 둘 중 하나만 입력해주세요")
    @PatchMapping("/rooms/unpin")
    public ResponseEntity <?> unpinChatRoom(
            @RequestParam(required = false) Long bandId,
            @RequestParam(required = false) Long chatId,
            HttpServletRequest request
    ) {
        String token = JwtTokenUtil.extractToken(request);
        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
        if( bandId == null && chatId != null) {
            return ResponseEntity.ok(chatRoomService.unpinChatRoom( chatId, currentMemberId));
        }else if(bandId != null && chatId == null) {
            return ResponseEntity.ok(chatRoomService.unpinBandChatRoom(bandId,currentMemberId));
        }else if(bandId != null && chatId != null) {
            return ResponseEntity.badRequest().body("하나만 입력해주세요");
        }
        else{
            return ResponseEntity.badRequest().body("bandId or chatId must be provided");
        }
    }






//    @Operation(summary = "밴드 지원하기")
//    @PostMapping("/bands/{bandId}/join")
//    public ResponseEntity<BandJoinResponse> joinBand(
//            @PathVariable Long bandId,
//            @RequestBody @Valid BandJoinRequest bandJoinRequest,
//            HttpServletRequest request
//    ) {
//        String token = JwtTokenUtil.extractToken(request);
//        Long currentMemberId = jwtTokenUtil.getMemberIdFromToken(token);
//        return ResponseEntity.ok(chatRoomService.joinBand(bandId, currentMemberId, bandJoinRequest.getSession()));
//    }
}
