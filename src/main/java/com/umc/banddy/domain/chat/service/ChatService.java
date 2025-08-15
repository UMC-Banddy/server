package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.chat.repository.ChatRoomParticipantRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomRepository;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Status;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
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
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHAT_ROOM_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        return Pair.of(chatRoom, member);
    }

    public ChatRoom verifedChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHAT_ROOM_NOT_FOUND));
    }

    public ChatRoomParticipant verifiedParticipant(ChatRoom chatRoom, Member member, Status status) {
        return participantRepository.findByChatRoomAndMemberAndStatus(chatRoom, member,Status.ACTIVE)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPANT_NOT_FOUND));
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
        ChatRoomParticipant participant = participantRepository.findByChatRoomAndMemberAndStatus(chatRoom, member, Status.ACTIVE)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPANT_NOT_FOUND));

        participant.setLastReadAt(LocalDateTime.now());
        return participant;
    }

    public Set<String> getSubscribedUserEmails(Long roomId) {
        String topicDestination = "/topic/room/" + roomId;
        String queueDestination = "/user/queue/room/" + roomId;
        Set<String> userEmails = new HashSet<>();
        for (SimpUser user : simpUserRegistry.getUsers()) {
            for (SimpSession session : user.getSessions()) {
                for (SimpSubscription subscription : session.getSubscriptions()) {
                    String dest = subscription.getDestination();
                    if (topicDestination.equals(dest) || queueDestination.equals(dest)) {
                        userEmails.add(user.getName());
                    }
                }
            }
        }
        return userEmails;
    }

    public Set<String> getPrivateSubscribedUserEmails(Long roomId) {
        String queueDestination = "/user/queue/room/" + roomId;
        Set<String> userEmails = new HashSet<>();
        for (SimpUser user : simpUserRegistry.getUsers()) {
            for (SimpSession session : user.getSessions()) {
                for (SimpSubscription subscription : session.getSubscriptions()) {
                    String dest = subscription.getDestination();
                    if (queueDestination.equals(dest)) {
                        userEmails.add(user.getName());
                    }
                }
            }
        }
        return userEmails;
    }

    public Set<String> getGroupSubscribedUserEmails(Long roomId) {
        String queueDestination = "/topic/room/" + roomId;
        Set<String> userEmails = new HashSet<>();
        for (SimpUser user : simpUserRegistry.getUsers()) {
            for (SimpSession session : user.getSessions()) {
                for (SimpSubscription subscription : session.getSubscriptions()) {
                    String dest = subscription.getDestination();
                    if (queueDestination.equals(dest)) {
                        userEmails.add(user.getName());
                    }
                }
            }
        }
        return userEmails;
    }

}
