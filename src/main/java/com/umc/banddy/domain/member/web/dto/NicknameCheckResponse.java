package com.umc.banddy.domain.member.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameCheckResponse {
    private boolean available;  // true면 사용 가능, false면 중복
    private String message;
}
