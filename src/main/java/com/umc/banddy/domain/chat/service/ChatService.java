package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.web.dto.*;

import java.security.Principal;

public interface ChatService {

    ChatMessage saveMessage(Principal principal, ChatMessageRequest messageRequest, Long roomId);

    ChatMessageResponse chatToResponse(ChatMessage chatMessage);

    PrivateChatRoomResponse createPrivateChatRoom(Principal principal, PrivateChatRoomRequest request);

    String findReceiverEmail(Long receiverId);

    ChatRoomResponse createGroupChatRoom(ChatRoomRequest requset);

    Long extractRoomId(String destination);

    void markLastRead(Long roomId, String principalName);

    //String findReceiverId(Long roomId);
}
