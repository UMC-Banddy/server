package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebsocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;

    public <M> void topicMessage(Long roomId, M message){
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId,
                message
        );
    }
    public <M> void queueMessage(Long receiverId, Long roomId, M message ) {
        messagingTemplate.convertAndSendToUser(
                memberRepository.findEmailById(receiverId),
                "/queue/room/" + roomId,
                message
        );
    }
//    public void timeMarkBroadcast(Long roomId, Long memberId) {
//        TimeMarkResponse timeMarkResponse = TimeMarkResponse.builder()
//                .memberId(memberId)
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        messagingTemplate.convertAndSend(
//                "/topic/room/" + roomId,
//                timeMarkResponse
//        );
//    }

}
