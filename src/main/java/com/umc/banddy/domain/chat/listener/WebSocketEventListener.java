//package com.umc.banddy.domain.chat.listener;
//
//import com.umc.banddy.domain.chat.service.ChatService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.*;
//
//import java.security.Principal;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class WebSocketEventListener {
//
//    @Autowired
//    private ChatService chatService;
//
//    public enum SubscriptionState { SUBSCRIBED, UNSUBSCRIBED }
//
//    // 1) 클라이언트가 STOMP CONNECT 프레임을 보냈을 때
//    @EventListener
//    public void handleSessionConnectEvent(SessionConnectEvent event) {
//        System.out.println("▶▶▶ SessionConnectEvent 호출됨");
//        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
//        Map<String, Object> attrs = h.getSessionAttributes();
//        System.out.println("   sessionAttrs: " + attrs);
//        if (attrs != null && !attrs.containsKey("roomStates")) {
//            attrs.put("roomStates", new ConcurrentHashMap<Long, SubscriptionState>());
//            System.out.println("   roomStates 맵 초기화");
//        }
//    }
//
//    // 2) 클라이언트가 토픽을 구독했을 때
//    @EventListener
//    public void handleSubscribeEvent(SessionSubscribeEvent event) {
//        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
//        String subscriptionId = h.getSubscriptionId();       // ex) "sub-0"
//        String simpDest      = (String) event.getMessage()
//                .getHeaders()
//                .get(SimpMessageHeaderAccessor.DESTINATION_HEADER);
//        Long roomId         = chatService.extractRoomId(simpDest);
//
//        Map<String, Object> attrs = h.getSessionAttributes();
//        Map<Long, SubscriptionState> roomStates =
//                (Map<Long, SubscriptionState>) attrs.get("roomStates");
//        Map<String, Long> subscriptionMap =
//                (Map<String, Long>) attrs.get("subscriptionMap");
//
//        // 1) subscriptionId ↔ roomId 저장
//        subscriptionMap.put(subscriptionId, roomId);
//        // 2) roomStates 상태 업데이트
//        roomStates.put(roomId, SubscriptionState.SUBSCRIBED);
//
//    }
//
//    // 3) 클라이언트가 토픽 구독을 취소했을 때
//    @EventListener
//    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
//        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
//        String subscriptionId = h.getSubscriptionId();
//
//        Map<String, Object> attrs = h.getSessionAttributes();
//        Map<Long, SubscriptionState> roomStates =
//                (Map<Long, SubscriptionState>) attrs.get("roomStates");
//        Map<String, Long> subscriptionMap =
//                (Map<String, Long>) attrs.get("subscriptionMap");
//
//        // 1) subscriptionId로 roomId 찾기
//        Long roomId = subscriptionMap.remove(subscriptionId);
//        if (roomId == null) {
//            return;  // 알 수 없는 subscription 이면 무시
//        }
//
//        Principal user = h.getUser();
//        if (user != null && roomStates.get(roomId) == SubscriptionState.SUBSCRIBED) {
//            chatService.markLastRead(roomId, user.getName());
//        }
//
//        // 2) roomStates 상태 업데이트
//        roomStates.put(roomId, SubscriptionState.UNSUBSCRIBED);
//    }
//
//    // 4) 클라이언트 연결이 끊겼을 때
//    @EventListener
//    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
//        System.out.println("▶▶▶ SessionDisconnectEvent 호출됨");
//        Message<?> message = event.getMessage();
//        StompHeaderAccessor h = StompHeaderAccessor.wrap(message);
//
//        Map<String, Object> attrs = h.getSessionAttributes();
//        System.out.println("   sessionAttrs: " + attrs);
//        if (attrs == null || !attrs.containsKey("roomStates")) {
//            return;
//        }
//
//        @SuppressWarnings("unchecked")
//        Map<Long, SubscriptionState> states =
//                (Map<Long, SubscriptionState>) attrs.get("roomStates");
//        Principal user = h.getUser();
//        System.out.println("   User on disconnect: " + (user != null ? user.getName() : "null"));
//        if (user != null) {
//            states.forEach((rid, state) -> {
//                if (state == SubscriptionState.SUBSCRIBED) {
//                    chatService.markLastRead(rid, user.getName());
//                    System.out.println("   disconnect markLastRead for roomId=" + rid);
//                }
//            });
//        }
//        attrs.put("roomStates",       new ConcurrentHashMap<Long, SubscriptionState>());
//        attrs.put("subscriptionMap",  new ConcurrentHashMap<String, Long>());
//    }
//}
