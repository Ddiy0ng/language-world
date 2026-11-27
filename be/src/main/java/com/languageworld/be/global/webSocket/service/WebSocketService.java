package com.languageworld.be.global.webSocket.service;

import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.webSocket.dto.WebSocketEventResponseDto;
import com.languageworld.be.global.webSocket.enumGroup.WebSocketPurposeCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendToRoom(WebSocketPurposeCode webSocketPurposeCode, Long roomId, WebSocketEventResponseDto<?> webSocketEventResponseDto) {

        String destination = switch (webSocketPurposeCode) {
            case CHAT -> WebSocketUtil.CHAT_SUB_PREFIX + roomId;
            case GAME -> WebSocketUtil.GAME_SUB_PREFIX + roomId;
            case GAME_USER -> {

                CustomLogger.error(
                        LogEventCode.WEBSOCKET,
                        "FAIL",
                        LogEventReasonCode.REQUIRE_SEND_TO_GAME_USER_METHOD,
                        LogEventReasonCode.REQUIRE_SEND_TO_GAME_USER_METHOD.getMessage() + "sendToRoom()를 호출할 수 없는 purpose입니다.",
                        null
                );

                throw new CustomException(CustomExceptionCode.INTERNAL_SERVER_ERROR);
            }
            // 게임방 목록은 sendToGameroomList로 보냄
            case GAMEROOM_LIST -> throw new CustomException(CustomExceptionCode.INTERNAL_SERVER_ERROR);
        };

        simpMessagingTemplate.convertAndSend(destination, webSocketEventResponseDto);
    }

    // 게임방 목록 구독자 전체: /sub/gameroomList
    public void sendToGameroomList(WebSocketEventResponseDto<?> webSocketEventResponseDto) {
        simpMessagingTemplate.convertAndSend(WebSocketUtil.GAMEROOM_LIST_SUB_DESTINATION, webSocketEventResponseDto);
    }

    public void sendToGameUser(Long gameroomId, UUID userUuid, WebSocketEventResponseDto<?> webSocketEventResponseDto) {
        simpMessagingTemplate.convertAndSend(WebSocketUtil.GAME_SUB_PREFIX + gameroomId + WebSocketUtil.GAME_USER_SUB_INFIX + userUuid, webSocketEventResponseDto);
    }
}
