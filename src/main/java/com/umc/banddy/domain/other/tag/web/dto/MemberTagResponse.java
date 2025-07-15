package com.umc.banddy.domain.other.tag.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MemberTagResponse {

    private Long memberId;
    private List<String> tags;

    @Builder
    public MemberTagResponse(Long memberId, List<String> tags) {
        this.memberId = memberId;
        this.tags = tags;
    }
}
