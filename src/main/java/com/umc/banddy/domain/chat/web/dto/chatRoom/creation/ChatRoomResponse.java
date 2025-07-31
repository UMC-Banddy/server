package com.umc.banddy.domain.chat.web.dto.chatRoom.creation;

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
    //private LocalDateTime pinnedAt;
    private RoomType roomtype;
    private List<RoomMemberinfo> memberinfos;

    @Getter
    @Builder
    public static class RoomMemberinfo {
        private Long userId;
        private String userName;
    }
}
