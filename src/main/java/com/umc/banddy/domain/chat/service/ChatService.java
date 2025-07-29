package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.chat.repository.ChatMessageRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomParticipantRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomRepository;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Status;
import com.umc.banddy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final WebsocketService websocketService;
    private final ChatMessageRepository chatMessageRepository;

    public Pair<ChatRoom, Member> verifedChatRoomAndMember(Long roomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다. ID: " + roomId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다. ID: " + memberId));
        return Pair.of(chatRoom, member);
    }

    public ChatRoom verifedChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다. ID: " + roomId));
    }

    public ChatRoomParticipant verifedParticipant(ChatRoom chatRoom, Member member, Status status) {
        return participantRepository.findTopByChatRoomAndMemberAndStatusOrderByIdDesc(chatRoom, member,Status.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여자입니다. 채팅방 ID: " + chatRoom.getId() + ", 멤버 ID: " + member.getId()));
    }
    public Long extractRoomId(String dest) {
        if (dest == null) {
            return null;
        }
        String[] parts = dest.split("/");
        String last = parts[parts.length - 1];
        try {
            return Long.valueOf(last);
        } catch (NumberFormatException e) {
            // 경로 형식이 예상과 다를 경우 널 리턴 또는 예외 처리
            return null;
        }
    }
    @Transactional
    public ChatRoomParticipant markLastRead(ChatRoom chatRoom , Member member) {
        ChatRoomParticipant participant = participantRepository.findTopByChatRoomAndMemberAndStatusOrderByIdDesc(chatRoom, member, Status.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여자입니다."));

        participant.setLastReadAt(LocalDateTime.now());
        return participant;
    }

}
