package com.umc.banddy.domain.member.web.dto;

import com.umc.banddy.domain.member.enums.Platform;
import lombok.Getter;

@Getter
public class SnsLinkRequest {
    private Platform platform;
    private String url;
}
