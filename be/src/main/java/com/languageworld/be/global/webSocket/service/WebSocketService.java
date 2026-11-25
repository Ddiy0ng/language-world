package com.languageworld.be.global.webSocket.service;

import com.languageworld.be.global.webSocket.dto.WebSocketEventResponseDto;
import com.languageworld.be.global.webSocket.enumGroup.WebSocketPurposeCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendToRoom(WebSocketPurposeCode webSocketPurposeCode, Long roomId, WebSocketEventResponseDto<?> webSocketEventResponseDto) {

        String destination = switch (webSocketPurposeCode) {
            case CHAT -> WebSocketUtil.CHAT_SUB_PREFIX + roomId;
            case GAME -> WebSocketUtil.GAME_SUB_PREFIX + roomId;
        };

        simpMessagingTemplate.convertAndSend(destination, webSocketEventResponseDto);
    }
}
