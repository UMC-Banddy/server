package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.repository.ChatMessageRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomParicipantRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomRepository;
import com.umc.banddy.domain.chat.repository.UserTempRepository;
import com.umc.banddy.domain.chat.web.dto.ChatMessageRequest;
import com.umc.banddy.domain.chat.web.dto.ChatMessageResponse;
import com.umc.banddy.domain.chat.web.dto.MessageAuthenticationHeader;
import com.umc.banddy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.servlet.GraphQlWebMvcAutoConfiguration;
import org.springframework.stereotype.Service;

import java.security.Principal;

import static com.umc.banddy.domain.chat.converter.chatConveter.toChatMessageResponse;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParicipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

//    public void verifyParticipant() {
    //        // 방 형식 검증
//        if (messageRequest.getRoomType() == null) {
//            throw new IllegalArgumentException("방 형식이 지정되지 않았습니다");
//        }
//
//        // 공통: 방 존재 확인
//        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
//        if (roomOpt.isEmpty()) {
//            return;
//        }
//
//        Long senderId = messageRequest.getSenderId();
//        // 공통: 참여자 검증
//        Optional<ChatRoomParticipant> participantOpt =
//                participantRepository.findByRoomIdAndUserId(roomId, senderId);
//        if (participantOpt.isEmpty()) {
//            return;
//        }
//    }

    public ChatMessage saveMessage(
            Principal principal,
            ChatMessageRequest messageRequest ,
            Long roomId
    ){
        MessageAuthenticationHeader auth = (MessageAuthenticationHeader) principal;

        ChatMessage chatMessage = ChatMessage.builder()
                .member(memberRepository.findById(auth.getMemberId())
                        .orElseThrow(() -> new IllegalArgumentException("잘못된 참여자")))
                .chatRoom(chatRoomRepository.findById(roomId)
                        .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다.")))
                .content(messageRequest.getContent())
                .build();

        return chatMessageRepository.save(chatMessage);
    }

    public ChatMessageResponse chatToResponse(ChatMessage chatMessage) {
        return toChatMessageResponse(chatMessage);
    }
}
