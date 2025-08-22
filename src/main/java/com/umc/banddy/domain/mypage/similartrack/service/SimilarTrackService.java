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

    public List<SimilarTrackResponse> getTracksSavedBySimilarUsers(Long loginMemberId) {
        Member loginMember = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 1) 기존 유틸로 유사 유저
        var similarUsers = similarityUtil.findSimilarMembers(loginMember);

        List<Track> tracks;

        if (similarUsers != null && !similarUsers.isEmpty()) {
            tracks = memberTrackRepository.findTopSavedTracksByMembers(
                    similarUsers,
                    loginMemberId,
                    PageRequest.of(0, 5)
            );

            // 2) 유틸 결과가 비어있거나 곡이 안 나오면 저장 트랙 겹침 기반 fallback
            if (tracks.isEmpty()) {
                tracks = fallbackByTrackOverlap(loginMemberId);
            }
        } else {
            // 3) 처음부터 유사 유저가 안 잡히면 fallback
            tracks = fallbackByTrackOverlap(loginMemberId);
        }

        return tracks.stream()
                .map(SimilarTrackConverter::toResponse)
                .collect(Collectors.toList());
    }

    private List<Track> fallbackByTrackOverlap(Long loginMemberId) {
        // 내 저장곡과 "최소 1곡" 이상 겹치는 유저 상위 50명 내에서 추천곡 Top 5
        List<Long> similarIds = memberTrackRepository.findSimilarMemberIdsByTrackOverlap(
                loginMemberId, 1L, PageRequest.of(0, 50));

        if (similarIds.isEmpty()) return List.of();

        return memberTrackRepository.findTopSavedTracksByMemberIds(
                similarIds, loginMemberId, PageRequest.of(0, 5));
    }
}
