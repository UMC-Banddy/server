package com.umc.banddy.domain.mypage.profile.web.controller;

import com.umc.banddy.domain.mypage.profile.service.MyProfileMediaService;
import com.umc.banddy.domain.mypage.profile.web.dto.ProfileMediaUploadResponse;
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

    @PostMapping("/media")
    public ResponseEntity<ProfileMediaUploadResponse> uploadProfileMedia(
            @RequestPart("file") MultipartFile file,
            HttpServletRequest request
    ) {
        String url = myProfileMediaService.uploadProfileMedia(file, request);
        return ResponseEntity.ok(new ProfileMediaUploadResponse(url));
    }
}
