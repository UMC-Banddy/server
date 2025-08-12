package com.umc.banddy.domain.mypage.profile.web.controller;

import com.umc.banddy.domain.mypage.profile.service.MyProfileMediaService;
import com.umc.banddy.domain.mypage.profile.web.dto.ProfileMediaUploadResponse;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Profile("s3")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class MyProfileMediaController {

    private final MyProfileMediaService myProfileMediaService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/media")
    public ResponseEntity<ProfileMediaUploadResponse> uploadProfileMedia(
            @RequestPart("file") MultipartFile file,
            HttpServletRequest request
    ) {
        if (file == null || file.isEmpty()) throw new GeneralException(ErrorStatus._BAD_REQUEST);
        String token = JwtTokenUtil.extractToken(request);
        if (token == null || token.isBlank()) throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        try { jwtTokenUtil.getMemberIdFromToken(token); } catch (Exception e) { throw new GeneralException(ErrorStatus._UNAUTHORIZED); }
        String url = myProfileMediaService.uploadProfileMedia(file, request);
        return ResponseEntity.ok(new ProfileMediaUploadResponse(url));
    }
}
