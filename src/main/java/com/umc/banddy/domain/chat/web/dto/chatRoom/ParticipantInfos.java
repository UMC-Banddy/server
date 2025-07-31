package com.umc.banddy.domain.chat.web.dto.chatRoom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ParticipantInfos {

    private Long roomId;

    private List<Info> infos;


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Info{

        private Long memberId;
        private LocalDateTime timestamp;
    }

}
