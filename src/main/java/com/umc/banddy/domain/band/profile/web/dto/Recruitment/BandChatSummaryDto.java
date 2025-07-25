package com.umc.banddy.domain.band.profile.web.dto.Recruitment;

import com.umc.banddy.domain.chat.domain.enums.PassFail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@Builder
public class BandChatSummaryDto{

    private Long roomId;

    private String nickname;

    private String imageUrl;

    private String session;

    private String content;

    private LocalDateTime lastMessageAt;

    private PassFail isPass;
}