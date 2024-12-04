package com.chessprojectspring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); // 서버에서 클라이언트로 메시지를 보낼 때 사용하는 prefix
        config.setApplicationDestinationPrefixes("/pub"); // 클라이언트에서 서버로 메시지를 보낼 때 사용하는 prefix
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connection")
        .setAllowedOriginPatterns("*") // 모든 출처 허용 옵션
        .withSockJS(); // 클라이언트에서 서버로 메시지를 보낼 때 사용하는 endpoint

        registry.addEndpoint("/connection")
        .setAllowedOriginPatterns("*");
    }
} 