//package com.umc.banddy.domain.chat.web.controller;
//
//import com.umc.banddy.domain.chat.service.ChatService;
//import com.umc.banddy.domain.chat.service.RecruitmentService;
//import com.umc.banddy.domain.chat.web.dto.*;
//import io.swagger.v3.oas.annotations.Operation;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("api")
//public class RecruitmentController {
//
//    private final ChatService chatService;
//    private final RecruitmentService recruitmentService;
//
//
//    @Operation(summary = "모집방 생성" , description = "모집방 생성 api")
//    @PostMapping("/recruitment")
//    public ResponseEntity<RecruitmentResponse> createRecruitmentRoom(
//            @RequestBody @Valid RecruitmentRequest request) {
//        return ResponseEntity.ok(recruitmentService.createBand(request));
//    }
//
//
//
//}
