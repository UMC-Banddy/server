package com.umc.banddy.domain.chat.web.dto.chatroom.creation;

import com.umc.banddy.domain.chat.domain.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ChatRoomResponse {

    private Long roomId;
    private String roomName;
    private String roomImageUrl;
    private LocalDateTime lastMessageTime;
    private RoomType roomtype;
    private List<RoomMemberInfo> memberInfos;

    @Getter
    @Builder
    public static class RoomMemberInfo {
        private Long memberId;
        private String memberName;
    }
}
