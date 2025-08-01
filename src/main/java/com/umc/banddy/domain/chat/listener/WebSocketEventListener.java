package com.umc.banddy.domain.chat.listener;

import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.service.ChatService;
import com.umc.banddy.domain.chat.service.SubscriptionStateService;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SubscriptionStateService subscriptionStateService;
    private final ChatService chatService;
    private final JwtTokenUtil jwtTokenUtil;


    @EventListener
    public void handleSessionConnectedEvent(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        subscriptionStateService.setConnected(sessionId);
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        subscriptionStateService.setSubscribed(sessionId);
    }

    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        subscriptionStateService.setUnsubscribed(sessionId);
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        Long roomId = chatService.extractRoomId(accessor.getDestination());
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token != null && subscriptionStateService.isSubscribed(sessionId)) {
            Long memberId = jwtTokenUtil.getMemberIdFromToken(token);
            Pair<ChatRoom, Member> p = chatService.verifedChatRoomAndMember(roomId, memberId);
            chatService.markLastRead(p.getLeft(), p.getRight());
        }
        subscriptionStateService.removeSession(sessionId);
    }
}
