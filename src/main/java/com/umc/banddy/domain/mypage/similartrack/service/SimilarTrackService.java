package com.umc.banddy.domain.mypage.similartrack.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.repository.MemberTrackRepository;
import com.umc.banddy.domain.mypage.similartrack.converter.SimilarTrackConverter;
import com.umc.banddy.domain.mypage.similartrack.web.dto.SimilarTrackResponse;
import com.umc.banddy.global.util.MemberSimilarityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimilarTrackService {

    private final MemberRepository memberRepository;
    private final MemberTrackRepository memberTrackRepository;
    private final MemberSimilarityUtil similarityUtil;

    //유사 유저들이 저장한 트랙 인기순 상위 5개
    public List<SimilarTrackResponse> getTracksSavedBySimilarUsers(Long loginMemberId) {
        Member loginMember = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        var similarUsers = similarityUtil.findSimilarMembers(loginMember);
        if (similarUsers == null || similarUsers.isEmpty()) {
            return List.of();
        }

        List<Track> tracks = memberTrackRepository.findTopSavedTracksByMembers(
                similarUsers,            // 유사 회원 목록
                loginMemberId,              // 현재 로그인한 회원 ID
                PageRequest.of(0, 5)     // 상위 5개
        );

        return tracks.stream()
                .map(SimilarTrackConverter::toResponse)
                .collect(Collectors.toList());
    }

}
