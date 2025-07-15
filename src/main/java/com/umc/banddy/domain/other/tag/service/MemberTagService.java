package com.umc.banddy.domain.other.tag.service;

import com.umc.banddy.domain.other.tag.repository.MemberTagRepository;
import com.umc.banddy.domain.other.tag.converter.TagConverter;
import com.umc.banddy.domain.other.tag.web.dto.MemberTagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberTagService {

    private final MemberTagRepository memberTagRepository;

    public MemberTagResponse getTagsByMemberId(Long memberId) {
        return TagConverter.toResponse(
                memberId,
                memberTagRepository.findByMemberId(memberId)
        );
    }
}
