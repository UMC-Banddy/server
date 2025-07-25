package com.umc.banddy.domain.chat.web.dto;

import lombok.*;

import java.security.Principal;


@Getter
@AllArgsConstructor
@Builder
public class MessageAuthenticationHeader implements Principal {
    private final Long memberId;
    private final String name;
}
