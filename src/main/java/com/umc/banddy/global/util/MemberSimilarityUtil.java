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

    /**
     * 기준 유저와 유사한 유저들을 간단한 기준으로 찾아냄 (예: 태그 기반)
     */
    public List<Member> findSimilarMembers(Member me) {
        List<MemberTag> myTags = memberTagRepository.findByMember(me);
        Set<String> myTagNames = myTags.stream()
                .map(MemberTag::getTagName)
                .collect(Collectors.toSet());

        // 모든 유저 가져오기
        List<Member> allMembers = memberTagRepository.findAllMembersExcept(me.getId());

        // 유사도 점수 매핑
        Map<Member, Integer> similarityMap = new HashMap<>();

        for (Member other : allMembers) {
            List<MemberTag> otherTags = memberTagRepository.findByMember(other);
            Set<String> otherTagNames = otherTags.stream()
                    .map(MemberTag::getTagName)
                    .collect(Collectors.toSet());

            int common = 0;
            for (String tag : otherTagNames) {
                if (myTagNames.contains(tag)) common++;
            }

            if (common > 0) similarityMap.put(other, common);  // 공통 태그 ≥ 1인 유저만
        }

        return similarityMap.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())  // 유사도 내림차순 정렬
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
