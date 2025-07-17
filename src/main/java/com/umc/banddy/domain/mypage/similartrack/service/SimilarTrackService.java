package com.umc.banddy.domain.mypage.similartrack.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.repository.MemberTrackRepository;
import com.umc.banddy.domain.mypage.similartrack.converter.SimilarTrackConverter;
import com.umc.banddy.domain.mypage.similartrack.web.dto.SimilarTrackResponse;
import com.umc.banddy.global.util.MemberSimilarityUtil;
import lombok.RequiredArgsConstructor;
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

        // 유사 유저 추출
        List<Member> similarUsers = similarityUtil.findSimilarMembers(loginMember);

        // 유사 유저들이 저장한 트랙 인기순 정렬 후 상위 5개 추출
        List<Track> tracks = memberTrackRepository.findTopSavedTracksByMembers(similarUsers, 5);

        return tracks.stream()
                .map(SimilarTrackConverter::toResponse)
                .collect(Collectors.toList());
    }
}
