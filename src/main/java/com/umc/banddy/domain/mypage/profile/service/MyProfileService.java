package com.umc.banddy.domain.mypage.profile.service;

import com.umc.banddy.domain.member.enums.Gender;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.track.repository.MemberTrackRepository;
import com.umc.banddy.domain.other.tag.repository.MemberTagRepository;
import com.umc.banddy.domain.other.tag.domain.mapping.MemberTag;
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
    private final MemberTagRepository memberTagRepository;

    public MyProfileResponse getMyProfile(Long memberId) {
        List<MemberTrack> allTracks = memberTrackRepository.findAll(); // 전체 가져오기

        List<MemberTrack> filteredTracks = allTracks.stream()
                .filter(t -> t.getMember().getId().equals(memberId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())) // 최신순 정렬
                .limit(3)
                .collect(Collectors.toList());

        List<MyProfileResponse.SavedTrack> savedTracks = filteredTracks.stream()
                .map(t -> new MyProfileResponse.SavedTrack(
                        t.getTrack().getTitle(),
                        t.getTrack().getImageUrl()))
                .collect(Collectors.toList());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<String> tags = memberTagRepository.findByMemberId(memberId)
                .stream()
                .map(MemberTag::getTagName)
                .collect(Collectors.toList());

        return new MyProfileResponse(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getBio(),
                tags,
                savedTracks
        );
    }

    public void updateMyProfile(Long memberId, MyProfileUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        member = Member.builder()
                .id(member.getId())
                .nickname(request.getNickname())
                .age(request.getAge())
                .gender(Gender.valueOf(request.getGender()))
                .region(request.getRegion())
                .district(request.getDistrict())
                .bio(request.getBio())
                .profileImageUrl(member.getProfileImageUrl()) // 기존 이미지 유지
                .build();

        memberRepository.save(member);
    }
}
