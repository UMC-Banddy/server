package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.web.dto.ChatMessageRequest;
import com.umc.banddy.domain.chat.web.dto.ChatMessageResponse;

public interface ChatService {

    ChatMessage saveMessage(ChatMessageRequest messageRequest, Long roomId);

    ChatMessageResponse chatToResponse(ChatMessage chatMessage);
}
