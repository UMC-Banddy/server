package com.umc.banddy.domain.member.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {
    private Long memberId;
    private String email;
}
