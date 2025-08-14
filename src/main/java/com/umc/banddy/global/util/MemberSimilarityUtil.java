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
        // 1) 내 태그
        Set<String> myTagNames = memberTagRepository.findByMemberId(me.getId())
                .stream()
                .map(MemberTag::getTagName)
                .collect(Collectors.toSet());

        // 2) 후보 회원
        List<Member> candidates = memberTagRepository.findAllMembersExcept(me.getId());
        if (candidates.isEmpty()) return List.of();

        List<Long> candidateIds = candidates.stream().map(Member::getId).toList();

        // 3) 후보들의 태그를 한 번에 IN 조회 → N+1 제거
        Map<Long, Set<String>> tagsByMember = memberTagRepository.findByMemberIdIn(candidateIds)
                .stream()
                .collect(Collectors.groupingBy(
                        mt -> mt.getMember().getId(),
                        Collectors.mapping(MemberTag::getTagName, Collectors.toSet())
                ));

        Map<Member, Integer> similarityMap = new HashMap<>();

        // 4) 태그 기반 유사도
        if (!myTagNames.isEmpty()) {
            for (Member other : candidates) {
                Set<String> otherTags = tagsByMember.getOrDefault(other.getId(), Collections.emptySet());
                if (!otherTags.isEmpty()) {
                    int common = 0;
                    // 더 작은 집합을 순회
                    Set<String> smaller = (myTagNames.size() <= otherTags.size()) ? myTagNames : otherTags;
                    Set<String> larger  = (smaller == myTagNames) ? otherTags : myTagNames;
                    for (String t : smaller) if (larger.contains(t)) common++;
                    if (common > 0) similarityMap.put(other, common);
                }
            }
        }

        // 5) 태그로도 없으면 → 트랙 기반: 후보들의 트랙을 "한 번에" IN 조회
        if (similarityMap.isEmpty()) {
            Set<Long> myTrackIds = memberTrackRepository.findAllByMember(me)
                    .stream()
                    .map(mt -> mt.getTrack().getId())
                    .collect(Collectors.toSet());

            if (!myTrackIds.isEmpty()) {
                Map<Long, Set<Long>> tracksByMember = memberTrackRepository.findByMemberIdIn(candidateIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                mt -> mt.getMember().getId(),
                                Collectors.mapping(mt -> mt.getTrack().getId(), Collectors.toSet())
                        ));

                for (Member other : candidates) {
                    Set<Long> otherTrackIds = tracksByMember.getOrDefault(other.getId(), Collections.emptySet());
                    if (!otherTrackIds.isEmpty()) {
                        int commonTracks = 0;
                        Set<Long> smaller = (myTrackIds.size() <= otherTrackIds.size()) ? myTrackIds : otherTrackIds;
                        Set<Long> larger  = (smaller == myTrackIds) ? otherTrackIds : myTrackIds;
                        for (Long id : smaller) if (larger.contains(id)) commonTracks++;
                        if (commonTracks > 0) similarityMap.put(other, commonTracks);
                    }
                }
            }
        }

        // 6) 스코어 없으면 후보에서 최대 10명 리턴
        if (similarityMap.isEmpty()) {
            return candidates.stream().limit(10).toList();
        }

        // 7) 점수 내림차순 상위 10명
        return similarityMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();
    }
}
