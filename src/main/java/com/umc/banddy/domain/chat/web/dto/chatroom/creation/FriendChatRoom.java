package com.umc.banddy.domain.chat.web.dto.chatroom.creation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FriendChatRoom {
    private Long roomId;           // 채팅방 ID
    private Long memberId;
    private String friendName;     // 상대방 이름
    private String profileImage;   // 상대방 프로필 사진 URL
}
