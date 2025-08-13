package com.umc.banddy.global.util;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import com.umc.banddy.domain.other.profile.domain.mapping.MemberTag;
import com.umc.banddy.domain.other.profile.repository.MemberTagRepository;
import com.umc.banddy.domain.music.track.repository.MemberTrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemberSimilarityUtil {

    private final MemberTagRepository memberTagRepository;
    private final MemberTrackRepository memberTrackRepository;

    public List<Member> findSimilarMembers(Member me) {
        //1) 태그 기반 유사도
        Set<String> myTagNames = memberTagRepository.findByMember(me)
                .stream().map(MemberTag::getTagName).collect(Collectors.toSet());

        List<Member> candidates = memberTagRepository.findAllMembersExcept(me.getId());
        Map<Member, Integer> similarityMap = new HashMap<>();

        for (Member other : candidates) {
            Set<String> otherTagNames = memberTagRepository.findByMember(other)
                    .stream().map(MemberTag::getTagName).collect(Collectors.toSet());

            int common = (int) otherTagNames.stream().filter(myTagNames::contains).count();
            if (common > 0) similarityMap.put(other, common);
        }

        // 2) 태그 결과가 없으면 트랙 기반
        if (similarityMap.isEmpty()) {
            // 내 트랙 세트
            Set<Long> myTrackIds = memberTrackRepository.findAllByMember(me)
                    .stream().map(MemberTrack::getTrack).map(t -> t.getId()).collect(Collectors.toSet());

            for (Member other : candidates) {
                Set<Long> otherTrackIds = memberTrackRepository.findAllByMember(other)
                        .stream().map(MemberTrack::getTrack).map(t -> t.getId()).collect(Collectors.toSet());

                // 교집합 크기
                int commonTracks = 0;
                if (!myTrackIds.isEmpty() && !otherTrackIds.isEmpty()) {
                    // 더 작은 쪽을 순회하면 약간 효율적
                    Set<Long> smaller = (myTrackIds.size() <= otherTrackIds.size()) ? myTrackIds : otherTrackIds;
                    Set<Long> larger  = (smaller == myTrackIds) ? otherTrackIds : myTrackIds;
                    for (Long id : smaller) if (larger.contains(id)) commonTracks++;
                }

                if (commonTracks > 0) {
                    similarityMap.put(other, commonTracks);
                }
            }
        }

        // 3) 그래도 비면: 나를 제외한 상위 N(최근/임의) 반환
        if (similarityMap.isEmpty()) {
            // 최근/임의 N명 반환 (여기선 candidates 앞에서부터 10명)
            return candidates.stream().limit(10).collect(Collectors.toList());
        }

        // 유사도 점수 내림차순 정렬 후 상위 10명
        return similarityMap.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
