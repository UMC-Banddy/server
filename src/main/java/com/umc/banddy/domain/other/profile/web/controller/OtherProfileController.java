package com.umc.banddy.domain.other.profile.web.controller;

import com.umc.banddy.domain.other.profile.service.OtherProfileService;
import com.umc.banddy.domain.other.profile.web.dto.OtherProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class OtherProfileController {

    private final OtherProfileService otherProfileService;

    @GetMapping("/{memberId}/profile")
    public ResponseEntity<OtherProfileResponse> getOtherProfile(@PathVariable Long memberId) {
        return ResponseEntity.ok(otherProfileService.getOtherProfile(memberId));
    }
}
