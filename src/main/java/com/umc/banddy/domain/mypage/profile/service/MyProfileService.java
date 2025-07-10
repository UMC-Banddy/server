package com.umc.banddy.domain.mypage.profile.service;

import com.umc.banddy.domain.mypage.profile.domain.mapping.MemberTrack;
import com.umc.banddy.domain.mypage.profile.domain.Member;
import com.umc.banddy.domain.mypage.profile.repository.MemberRepository;
import com.umc.banddy.domain.mypage.profile.repository.MemberTrackRepository;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileResponse;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyProfileService {

    private final MemberRepository memberRepository;
    private final MemberTrackRepository memberTrackRepository;

    public MyProfileResponse getMyProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<MemberTrack> tracks = memberTrackRepository.findTop3ByMemberIdOrderByCreatedAtDesc(memberId);
        List<MyProfileResponse.SavedTrack> savedTracks = tracks.stream()
                .map(t -> new MyProfileResponse.SavedTrack(
                        t.getTrack().getTitle(),
                        t.getTrack().getImageUrl()))
                .collect(Collectors.toList());

        boolean soundOn = tracks.stream()
                .anyMatch(t -> "ON".equalsIgnoreCase(t.getSoundOn()));

        return new MyProfileResponse(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getBio(),
                List.of("태그A", "태그B"), // TODO: 태그 로직 추가
                savedTracks,
                soundOn
        );
    }

    public void updateMyProfile(Long memberId, MyProfileUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        member = Member.builder()
                .id(member.getId())
                .nickname(request.getNickname())
                .age(request.getAge())
                .gender(request.getGender())
                .region(request.getRegion())
                .district(request.getDistrict())
                .bio(request.getBio())
                .profileImageUrl(member.getProfileImageUrl()) // 기존 이미지 유지
                .build();

        memberRepository.save(member);
    }
}
