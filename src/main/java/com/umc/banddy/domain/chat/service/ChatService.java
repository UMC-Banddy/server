package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.web.dto.*;
import com.umc.banddy.domain.member.domain.Member;
import org.apache.commons.lang3.tuple.Pair;

import java.security.Principal;

public interface ChatService {

    Pair<ChatRoom, Member> verifedRoomAndMember(Long roomId, Long memberId);

    ChatMessage saveMessage(Principal principal, ChatMessageRequest messageRequest, Long roomId);

    ChatMessageResponse chatToResponse(ChatMessage chatMessage);

    PrivateChatRoomResponse createPrivateChatRoom(Principal principal, PrivateChatRoomRequest request);

    String findReceiverEmail(Long receiverId);

    ChatRoomResponse createGroupChatRoom(Long memberId, ChatRoomRequest requset);

    Long extractRoomId(String destination);

    void markLastRead(Long roomId, String principalName);

    ChatSystemResponse joinChatRoom(ChatRoom chatRoom, Member member);

    void topicMessage(Long roomId, ChatMessageResponse chatMessageResponse);

    void queueMessage(String receiverEmail, Long roomId, ChatMessageResponse chatMessageResponse )


    //String findReceiverId(Long roomId);
}
