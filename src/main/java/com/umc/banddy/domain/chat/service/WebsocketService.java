package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebsocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public <M> void topicMessage(Long roomId, M message){
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId,
                message
        );
    }
    public <M> void queuePrivateMessage(String receiverEmail, Long roomId, M message ) {
        messagingTemplate.convertAndSendToUser(
                receiverEmail,
                "/queue/room/" + roomId,
                message
        );
    }
    public <M> void queueUnreadMessage(String receiverEmail, M message ) {
        messagingTemplate.convertAndSendToUser(
                receiverEmail,
                "/queue/unread",
                message
        );
    }

}
