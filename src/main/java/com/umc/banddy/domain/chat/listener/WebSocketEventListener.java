package com.umc.banddy.domain.chat.listener;


import com.umc.banddy.domain.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketEventListener {

    @Autowired
    private ChatService chatService;

    // Connettion 이벤트
    @EventListener
    public void handleSessionConnectedEvent(SessionConnectedEvent event) {
        // 세션 플래그 생성
        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attrs = h.getSessionAttributes();
        if (attrs == null) return;
        attrs.put("roomStates", new ConcurrentHashMap<Long, SubscriptionState>());
    }

    // Disconnection 이벤트
    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        // 세션 플래그 삭제
        // 퇴장 시작 저장 로직, 세션 플래그로 구독해제와 중복 처리 주의
        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attrs = h.getSessionAttributes();
        if (attrs == null) return;
        Long roomId = chatService.extractRoomId(h.getDestination());
        @SuppressWarnings("unchecked")
        Map<Long, SubscriptionState> states = (Map<Long, SubscriptionState>) attrs.get("roomStates");

        Principal user = h.getUser();
        if (user != null) {
            String principalName = user.getName();  // JWT에서 뽑은 memberId.toString() 또는 email
            if (states.get(roomId) == SubscriptionState.SUBSCRIBED ){
                chatService.markLastRead(roomId, principalName);
            }
        }
    }

    // Subscribe 이벤트
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        // 입장 시작 저장 로직
        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attrs = h.getSessionAttributes();
        if (attrs == null) return;
        Long roomId = chatService.extractRoomId(h.getDestination());
        @SuppressWarnings("unchecked")
        Map<Long, SubscriptionState> states =
                (Map<Long, SubscriptionState>) attrs.get("roomStates");
        states.put(roomId, SubscriptionState.SUBSCRIBED);
    }


    // Unsubscribe 이벤트
    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        // 입장 시작 저장 로직
        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attrs = h.getSessionAttributes();
        if (attrs == null) return;
        Long roomId = chatService.extractRoomId(h.getDestination());
        @SuppressWarnings("unchecked")
        Map<Long, SubscriptionState> states =
                (Map<Long, SubscriptionState>) attrs.get("roomStates");

        Principal user = h.getUser();
        if (user != null) {
            String principalName = user.getName();  // JWT에서 뽑은 memberId.toString() 또는 email
            if (states.get(roomId) == SubscriptionState.SUBSCRIBED ){
                chatService.markLastRead(roomId, principalName);
            }
        }

        states.put(roomId, SubscriptionState.UNSUBSCRIBED);
    }

    public enum SubscriptionState {
        SUBSCRIBED, UNSUBSCRIBED
    }

}
