package com.languageworld.be.global.config;

import com.languageworld.be.global.security.interceptor.WebSocketChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // channel interceptor
    private final WebSocketChannelInterceptor webSocketChannelInterceptor;

    // stomp 엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")
                .addInterceptors()
                .setAllowedOrigins("http://localhost:8080");

        // 한 연결에서 보낸 메시지를 보낸 순서대로 처리(그림 획 순서·진행 중 입장자 그림 복원)
        registry.setPreserveReceiveOrder(true);
    }

    // 구독 엔드포인트 설정 + 발행 엔드포인트 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
        // 보낸 순서대로 구독자에게 전달
        registry.setPreservePublishOrder(true);
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(webSocketChannelInterceptor);
    }
}
