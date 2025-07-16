package com.umc.banddy.domain.chat.web.controller;

import com.umc.banddy.domain.chat.service.ChatService;
import com.umc.banddy.domain.chat.web.dto.ChatRoomRequest;
import com.umc.banddy.domain.chat.web.dto.ChatRoomResponse;
import com.umc.banddy.domain.chat.web.dto.PrivateChatRoomRequest;
import com.umc.banddy.domain.chat.web.dto.PrivateChatRoomResponse;
import com.umc.banddy.domain.member.web.dto.NicknameCheckResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat")
public class ChatContoller {

    private final ChatService chatService;

    @Operation(summary = "채팅방 생성", description = "새로운 채팅방 생성 api")
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody @Valid ChatRoomRequest request) {
        return ResponseEntity.ok(chatService.createGroupChatRoom(request));
    }

//    @Operation(summary = "밴드/그룹 채팅방 조회", description = "밴드 또는 그룹 채팅방 조회 api")
//    @GetMapping("/rooms")
//    public ResponseEntity<> getChatRooms(){
//
//    }

    @Operation(summary = "개인 채팅방 생성", description = "개인 채팅방 생성 api")
    @PostMapping("/rooms/friends")
    public ResponseEntity<PrivateChatRoomResponse> createPrivateChatRooms(
            Principal principal,
            PrivateChatRoomRequest request
    ) {
        return ResponseEntity.ok(chatService.createPrivateChatRoom(principal,request));
    }
}
