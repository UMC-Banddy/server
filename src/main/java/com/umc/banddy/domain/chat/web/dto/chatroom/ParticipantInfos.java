package com.umc.banddy.domain.chat.web.dto.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ParticipantInfos {

    private List<Info> infos;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Info{

        private Long memberId;
        private String nickname;
        private String imageUrl;
        private LocalDateTime timestamp;
    }
}
