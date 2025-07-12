package com.umc.banddy.domain.chat.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat") // STOMP 엔드포인트 설정
                .setAllowedOrigins("*") // CORS 설정: 임시 모든 출처 허용
                .withSockJS(); // SockJS 적용

        // 순수 WebSocket 전용 엔드포인트
        registry.addEndpoint("/ws-chat-raw")
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic", "/queue","/user/queue"); // 메시지 브로커 설정
        // topic 구독자에게 브로드캐스트, queue는 특정 사용자에게 메시지 전송, /user/queue는 개인 메시지 전송을 위한 설정
    }
}

