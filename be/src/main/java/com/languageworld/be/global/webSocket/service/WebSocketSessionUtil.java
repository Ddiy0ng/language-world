package com.languageworld.be.global.webSocket.service;

import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.webSocket.entity.CustomWebSocketSession;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionUtil {

    private final Map<String, CustomWebSocketSession> webSocketSessionMap = new ConcurrentHashMap<>();

    public void registerWebSocketSession(String webSocketSessionId, CustomWebSocketSession customWebSocketSession) {

        webSocketSessionIdNullOrBlankCheck(webSocketSessionId);

        if(webSocketSessionMap.putIfAbsent(webSocketSessionId, customWebSocketSession) != null) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.DUPLICATE_WEBSOCKET_SESSION,
                    LogEventReasonCode.DUPLICATE_WEBSOCKET_SESSION.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
        }
    }

    public CustomWebSocketSession getValidWebSocketSession(String webSocketSessionId) {

        webSocketSessionIdNullOrBlankCheck(webSocketSessionId);

        CustomWebSocketSession customWebSocketSession = webSocketSessionMap.get(webSocketSessionId);

        // 세션 있나
        if(customWebSocketSession == null) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.NOT_REGISTERED_WEBSOCKET_SESSION,
                    LogEventReasonCode.NOT_REGISTERED_WEBSOCKET_SESSION.getMessage()
                        + " - webSocketSessionId: " + webSocketSessionId,
                    null
            );

            throw new CustomException(CustomExceptionCode.UNAUTHORIZED);
        }

        // 유효한가
        if(customWebSocketSession.expiration().toInstant().isBefore(Instant.now())) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.EXPIRED_TOKEN,
                    LogEventReasonCode.EXPIRED_TOKEN.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
        }

        return customWebSocketSession;
    }

    // 세션 갱신(토큰 재발급): 등록된 세션이고 같은 사용자일 때만, 만료된 세션도 갱신 가능
    public void renewWebSocketSession(String webSocketSessionId, CustomWebSocketSession customWebSocketSession) {

        webSocketSessionIdNullOrBlankCheck(webSocketSessionId);

        CustomWebSocketSession registeredWebSocketSession = webSocketSessionMap.get(webSocketSessionId);

        // 세션 있나
        if(registeredWebSocketSession == null) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.NOT_REGISTERED_WEBSOCKET_SESSION,
                    LogEventReasonCode.NOT_REGISTERED_WEBSOCKET_SESSION.getMessage()
                        + " - webSocketSessionId: " + webSocketSessionId,
                    null
            );

            throw new CustomException(CustomExceptionCode.UNAUTHORIZED);
        }

        // 같은 사용자인가
        if(!registeredWebSocketSession.customPrincipal().uuid().equals(customWebSocketSession.customPrincipal().uuid())) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.UNAUTHORIZED,
                    LogEventReasonCode.UNAUTHORIZED.getMessage()
                        + " - webSocketSessionId: " + webSocketSessionId,
                    null
            );

            throw new CustomException(CustomExceptionCode.UNAUTHORIZED);
        }

        webSocketSessionMap.put(webSocketSessionId, customWebSocketSession);
    }

    public void removeWebSocketSession(String webSocketSessionId) {

        webSocketSessionIdNullOrBlankCheck(webSocketSessionId);

        webSocketSessionMap.remove(webSocketSessionId);
    }

    // 같은 사용자의 다른 연결(다른 탭 등)이 남아 있는지
    public boolean hasWebSocketSessionByUserUuid(UUID userUuid) {
        return webSocketSessionMap.values().stream()
                .anyMatch(customWebSocketSession -> customWebSocketSession.customPrincipal().uuid().equals(userUuid));
    }

    private void webSocketSessionIdNullOrBlankCheck(String webSocketSessionId) {

        if(webSocketSessionId == null || webSocketSessionId.isBlank()) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.WEBSOCKET_SESSION_IS_NULL_OR_BLANK,
                    LogEventReasonCode.WEBSOCKET_SESSION_IS_NULL_OR_BLANK.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.UNAUTHORIZED);
        }
    }
}
