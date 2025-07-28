package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.chat.repository.ChatRoomParticipantRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomRepository;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Status;
import com.umc.banddy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final SimpUserRegistry simpUserRegistry;


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

    public Set<String> getSubscribedUserEmails(Long roomId) {
        String destination = "/topic/room/" + roomId;
        Set<String> userEmails = new HashSet<>();
        for (SimpUser user : simpUserRegistry.getUsers()) {
            for (SimpSession session : user.getSessions()) {
                for (SimpSubscription subscription : session.getSubscriptions()) {
                    if (destination.equals(subscription.getDestination())) {
                        userEmails.add(user.getName());
                    }
                }
            }
        }
        return userEmails;
    }

    public boolean isUserSubscribedToRoom( Long roomId, String userEmail) {
        String targetDestination = "/topic/room/" + roomId;

        SimpUser user = simpUserRegistry.getUser(userEmail);
        if (user == null) return false;

        for (SimpSession session : user.getSessions()) {
            for (SimpSubscription sub : session.getSubscriptions()) {
                if (targetDestination.equals(sub.getDestination())) {
                    return true;
                }
            }
        }

        return false;
    }


}
