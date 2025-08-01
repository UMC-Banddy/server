package com.umc.banddy.domain.chat.web.dto.chatroom.roomlist;


import com.umc.banddy.domain.band.profile.enums.BandStatus;
import com.umc.banddy.domain.chat.domain.enums.PassFail;
import com.umc.banddy.domain.chat.web.dto.chatroom.MemberInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuperBuilder
public class BandManagerRoomInfoDto extends ChatRoomInfo {


    private Long bandId;

    private String bandImageUrl;

    private String bandName;

    private BandStatus status;

    private List<String> bandSessionList;

    private List<BandChatRoomInfo> chatRoomInfo;


    @Getter
    @Builder
    public static class BandChatRoomInfo {
        private Long roomId;
        private MemberInfo memberInfo;
        private String session;
        private PassFail passFail;
        private LocalDateTime lastMessageAt;
        private Long UnreadCount;
    }
}
