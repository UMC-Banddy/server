package com.umc.banddy.domain.band.profile.web.dto.Recruitment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class RecruitmentResponse {

    private Long bandId; // 채팅방 ID
    private String bandName; // 밴드 이름
    private String profileImageUrl; // 프로필 이미지 URL
    private String type;
    private LocalDateTime createdAt; // 채팅방 생성 시간

}
