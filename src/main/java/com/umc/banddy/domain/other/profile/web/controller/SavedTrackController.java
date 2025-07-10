package com.umc.banddy.domain.other.profile.web.controller;

import com.umc.banddy.domain.other.profile.service.SavedTrackService;
import com.umc.banddy.domain.other.profile.web.dto.SavedTrackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class SavedTrackController {

    private final SavedTrackService savedTrackService;

    @GetMapping("/{memberId}/tracks")
    public ResponseEntity<List<SavedTrackResponse>> getSavedTracks(@PathVariable Long memberId) {
        return ResponseEntity.ok(savedTrackService.getSavedTracks(memberId));
    }
}

