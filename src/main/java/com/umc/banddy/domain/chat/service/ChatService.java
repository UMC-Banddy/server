package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.web.dto.ChatMessageRequest;
import com.umc.banddy.domain.chat.web.dto.ChatMessageResponse;

import java.security.Principal;

public interface ChatService {

    ChatMessage saveMessage(Principal principal, ChatMessageRequest messageRequest, Long roomId);

    ChatMessageResponse chatToResponse(ChatMessage chatMessage);
}
