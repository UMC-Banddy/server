package com.umc.banddy.domain.chat.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@Data
@RequiredArgsConstructor
@Getter
public class MessageAuthenticationHeader implements Principal {
    private final Long memberId;
    private final String name;

}
