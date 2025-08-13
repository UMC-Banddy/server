package com.umc.banddy.domain.chat.web.dto.chatroom;


import com.umc.banddy.domain.chat.web.dto.chatroom.roomlist.ChatRoomInfoDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BandChatRoomInfoDto extends ChatRoomInfoDto {


    private Long bandId;
}
