package com.umc.banddy.global.config;


import com.umc.banddy.domain.chat.web.dto.MessageAuthenticationHeader;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.security.Principal;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    private TaskScheduler messageBrokerTaskScheduler;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public void setMessageBrokerTaskScheduler(@Lazy TaskScheduler taskScheduler) {
        this.messageBrokerTaskScheduler = taskScheduler;
    }

    // WebSocket 엔드포인트, CORS, SockJS 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // STOMP 엔드포인트 설정
                .setAllowedOriginPatterns("*") // CORS 설정: 임시 모든 출처 허용
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .withSockJS(); // SockJS 적용

//        // 순수 WebSocket 전용 엔드포인트
//        registry.addEndpoint("/ws")
//                .setAllowedOriginPatterns("*");
    }

    // 메세지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[] {10000, 20000})   // [클라이언트->서버: 10초, 서버->클라이언트: 20초]
                .setTaskScheduler(this.messageBrokerTaskScheduler); // 메시지 브로커 설정
        // topic 구독자에게 브로드캐스트, queue는 특정 사용자에게 메시지 전송, /user/queue는 개인 메시지 전송을 위한 설정
        config.setUserDestinationPrefix("/user");
    }


    // 클라이언트로부터 수신 받은 메세지 인터셉터
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
       registration.interceptors(new ChannelInterceptor() {

           // 웹소켓 메세지는 시큐리티 체인을 통과하지 않아 인증 로직 구현
           // connect 요청시 한번만 jwt 토큰을 검증함
           @Override
           public Message<?> preSend(Message<?> message, MessageChannel channel) {
               StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
               if(StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
                   String token = headerAccessor.getFirstNativeHeader("Authorization");

                   if (token != null && token.startsWith("Bearer ")) {
                       token = token.substring(7);  // 'Bearer ' 잘라냄
                   }

                   if (token == null || token.isEmpty() || !jwtTokenUtil.validateToken(token)) {
                       throw new IllegalArgumentException("Authorization header 없음 또는 JWT 검증 실패");
                   } else {
                       Principal principal = new MessageAuthenticationHeader(
                               jwtTokenUtil.getMemberIdFromToken(token),
                               jwtTokenUtil.getEmailFromToken(token)
                       );
                       headerAccessor.setUser(principal);
                   }
               }
               return message;
           }
       });
    }
}

