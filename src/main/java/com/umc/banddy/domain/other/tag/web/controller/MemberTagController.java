package com.umc.banddy.domain.other.tag.web.controller;

import com.umc.banddy.domain.other.tag.service.MemberTagService;
import com.umc.banddy.domain.other.tag.web.dto.MemberTagResponse;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Tag(name = "사용자 태그 조회")
public class MemberTagController {

    private final MemberTagService memberTagService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/{memberId}/tags")
    public MemberTagResponse getMyTags(HttpServletRequest request) {
        String token = JwtTokenUtil.extractToken(request);
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
        return memberTagService.getTagsByMemberId(memberId);
    }
}