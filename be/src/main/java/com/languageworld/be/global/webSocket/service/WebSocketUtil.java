package com.languageworld.be.global.webSocket.service;

import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.webSocket.enumGroup.WebSocketPurposeCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class WebSocketUtil {

    private static final Pattern CHATROOM_SUB_PATTERN = Pattern.compile("^/sub/chatroomId/([1-9][0-9]*)$");
    private static final Pattern CHATROOM_PUB_PATTERN = Pattern.compile("^/pub/chatroomId/([1-9][0-9]*)$");
    private static final Pattern GAME_SUB_PATTERN = Pattern.compile("^/sub/gameroomId/([1-9][0-9]*)$");
    private static final Pattern GAME_PUB_PATTERN = Pattern.compile("^/pub/gameroomId/([1-9][0-9]*)/chat$");

    public static final String CHAT_SUB_PREFIX = "/sub/chatroomId/";
    public static final String CHAT_PUB_PREFIX = "/pub/chatroomId/";
    public static final String GAME_SUB_PREFIX = "/sub/gameroomId/";
    public static final String GAME_PUB_PREFIX = "/pub/gameroomId/";

    public String getDestination(StompHeaderAccessor stompHeaderAccessor) {

        String destination = stompHeaderAccessor.getDestination();
        if(destination == null || destination.isBlank()) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.DESTINATION_IS_NULL_OR_BLANK,
                    LogEventReasonCode.DESTINATION_IS_NULL_OR_BLANK.getMessage()
                            + " - destination: " + destination,
                    null
            );

            throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
        }

        return destination;
    }

    // 경로 용도 확인
    public WebSocketPurposeCode getWebSocketPurpose(String destination) {

        if(destination.startsWith(CHAT_SUB_PREFIX) || destination.startsWith(CHAT_PUB_PREFIX))
            return WebSocketPurposeCode.CHAT;
        else if(destination.startsWith(GAME_SUB_PREFIX) || destination.startsWith(GAME_PUB_PREFIX))
            return WebSocketPurposeCode.GAME;
        else {
            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.INVALID_WEBSOCKET_PREFIX,
                    LogEventReasonCode.INVALID_WEBSOCKET_PREFIX.getMessage()
                            + " - destination: " + destination,
                    null
            );

            throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
        }
    }

    // roomId 추출
    public Long extractRoomId(WebSocketPurposeCode webSocketPurposeCode, String destination, StompCommand stompCommand) {

        return switch(webSocketPurposeCode) {
            case CHAT -> extractIdByCommand(destination, stompCommand, CHATROOM_SUB_PATTERN, CHATROOM_PUB_PATTERN);
            case GAME -> extractIdByCommand(destination, stompCommand, GAME_SUB_PATTERN, GAME_PUB_PATTERN);
        };
    }

    // sub, pub에 따라 패턴 맞춰 id 추출
    private Long extractIdByCommand(String destination, StompCommand stompCommand,
                                    Pattern subPattern, Pattern pubPattern) {

        switch (stompCommand) {
            case SUBSCRIBE -> {
                return getDestinationIdIfDestAndPatternMatch(destination, subPattern);
            }
            case SEND -> {
                return getDestinationIdIfDestAndPatternMatch(destination, pubPattern);
            }
            default -> {

                CustomLogger.warn(
                        LogEventCode.WEBSOCKET,
                        "FAIL",
                        LogEventReasonCode.STOMP_COMMAND_NOT_REQUIRED_EXTRACT_ROOM_ID_METHOD,
                        LogEventReasonCode.STOMP_COMMAND_NOT_REQUIRED_EXTRACT_ROOM_ID_METHOD.getMessage(),
                        null
                );

                throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
            }
        }
    }

    private Long getDestinationIdIfDestAndPatternMatch(String destination, Pattern pattern) {

        Matcher matcher = pattern.matcher(destination);

        if(!matcher.matches()) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.DESTINATION_NOT_MATCH_WITH_PATTERN,
                    LogEventReasonCode.DESTINATION_NOT_MATCH_WITH_PATTERN.getMessage()
                            + " - destination: " + destination
                            + " - Pattern: " + pattern,
                    null
            );

            throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
        }

        return Long.valueOf(matcher.group(1));
    }

}
