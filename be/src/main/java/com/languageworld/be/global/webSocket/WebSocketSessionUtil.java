package com.languageworld.be.global.webSocket;

import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
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

    public void removeWebSocketSession(String webSocketSessionId) {

        webSocketSessionIdNullOrBlankCheck(webSocketSessionId);

        webSocketSessionMap.remove(webSocketSessionId);
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
