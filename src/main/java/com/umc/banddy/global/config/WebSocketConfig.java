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
import java.security.Principal;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private TaskScheduler messageBrokerTaskScheduler;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public void setMessageBrokerTaskScheduler(@Lazy TaskScheduler taskScheduler) {
        this.messageBrokerTaskScheduler = taskScheduler;
    }

    // WebSocket 엔드포인트, CORS, SockJS 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // STOMP 엔드포인트 설정
                .setAllowedOrigins("*") // CORS 설정: 임시 모든 출처 허용
                .withSockJS(); // SockJS 적용

        // 순수 WebSocket 전용 엔드포인트
        registry.addEndpoint("/ws-raw")
                .setAllowedOrigins("*");
    }

    // 메세지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic", "/queue","/user/queue")
                .setHeartbeatValue(new long[] {10000, 20000})   // [클라이언트->서버: 10초, 서버->클라이언트: 20초]              .setHeartbeatValue(new long[] {10000, 20000})   // [클라이언트->서버: 10초, 서버->클라이언트: 20초]
                .setTaskScheduler(this.messageBrokerTaskScheduler);; // 메시지 브로커 설정
        // topic 구독자에게 브로드캐스트, queue는 특정 사용자에게 메시지 전송, /user/queue는 개인 메시지 전송을 위한 설정
    }

    // 클라이언트로부터 수신 받은 메세지 인터셉터
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
       registration.interceptors(new ChannelInterceptor() {
           @Override
           public Message<?> preSend(Message<?> message, MessageChannel channel) {
               StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
               if(StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
                   String token = headerAccessor.getFirstNativeHeader("Authorization");
                   if (token != null && jwtTokenUtil.validateToken(token)) {
                       Principal principal = new MessageAuthenticationHeader(jwtTokenUtil.getMemberIdFromToken(token),jwtTokenUtil.getEmailFromToken(token));
                       headerAccessor.setUser(principal);
                   }
               }
//               else if (StompCommand.DISCONNECT.equals(headerAccessor.getCommand())) {
//                   // 연결 해제 시점에 필요한 로직 추가 가능
//                   // 예: 세션 정리, 사용자 상태 업데이트 등
//               }
               return message;
           }
       });
    }
}

