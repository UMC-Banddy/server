package com.umc.banddy.domain.other.tag.converter;

import com.umc.banddy.domain.other.tag.domain.mapping.MemberTag;
import com.umc.banddy.domain.other.tag.web.dto.MemberTagResponse;

import java.util.List;
import java.util.stream.Collectors;

public class TagConverter {

    public static MemberTagResponse toResponse(Long memberId, List<MemberTag> tags) {
        List<String> tagList = tags.stream()
                .map(MemberTag::getTag)
                .collect(Collectors.toList());

        return MemberTagResponse.builder()
                .memberId(memberId)
                .tags(tagList)
                .build();
    }
}
