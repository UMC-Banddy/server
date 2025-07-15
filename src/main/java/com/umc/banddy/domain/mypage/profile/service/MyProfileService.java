package com.umc.banddy.domain.mypage.profile.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Gender;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import com.umc.banddy.domain.music.track.repository.MemberTrackRepository;
import com.umc.banddy.domain.other.profile.domain.mapping.MemberTag;
import com.umc.banddy.domain.other.profile.repository.MemberTagRepository;
import com.umc.banddy.domain.mypage.profile.converter.MyProfileConverter;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileResponse;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileUpdateRequest;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyProfileService {

    private final MemberRepository memberRepository;
    private final MemberTrackRepository memberTrackRepository;
    private final MemberTagRepository memberTagRepository;
    private final JwtTokenUtil jwtTokenUtil;

    // 내 프로필 조회
    public MyProfileResponse getMyProfile(HttpServletRequest request) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(JwtTokenUtil.extractToken(request));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<MemberTag> tags = memberTagRepository.findByMemberId(memberId);

        List<MemberTrack> savedTracks = memberTrackRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .limit(3)
                .toList();

        return MyProfileConverter.toMyProfileResponse(member, tags, savedTracks);
    }

    // 내 프로필 수정
    public void updateMyProfile(HttpServletRequest request, MyProfileUpdateRequest dto) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(JwtTokenUtil.extractToken(request));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Member updated = Member.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .nickname(dto.getNickname())
                .age(dto.getAge())
                .gender(Gender.valueOf(dto.getGender()))
                .region(dto.getRegion())
                .district(dto.getDistrict())
                .bio(dto.getBio())
                .profileImageUrl(member.getProfileImageUrl())
                .mediaUrl(member.getMediaUrl())
                .refreshToken(member.getRefreshToken())
                .build();

        memberRepository.save(updated);
    }
}
