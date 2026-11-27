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

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class WebSocketUtil {

    private static final Pattern CHATROOM_SUB_PATTERN = Pattern.compile("^/sub/chatroomId/([1-9][0-9]*)$");
    private static final Pattern CHATROOM_PUB_PATTERN = Pattern.compile("^/pub/chatroomId/([1-9][0-9]*)$");
    private static final Pattern GAME_SUB_PATTERN = Pattern.compile("^/sub/gameroomId/([1-9][0-9]*)$");
    private static final Pattern GAME_PUB_PATTERN = Pattern.compile("^/pub/gameroomId/([1-9][0-9]*)/(chat|draw)$");
    private static final Pattern GAME_USER_SUB_PATTERN = Pattern.compile( "^/sub/gameroomId/([1-9][0-9]*)/userUuid/" + "([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$");
    public static final String CHAT_SUB_PREFIX = "/sub/chatroomId/";
    public static final String CHAT_PUB_PREFIX = "/pub/chatroomId/";
    public static final String GAME_SUB_PREFIX = "/sub/gameroomId/";
    public static final String GAME_PUB_PREFIX = "/pub/gameroomId/";
    public static final String GAME_USER_SUB_INFIX = "/userUuid/";
    public static final String GAMEROOM_LIST_SUB_DESTINATION = "/sub/gameroomList";
    // 토큰 재발급 뒤 연결을 유지한 채 인증만 갱신(인터셉터에서 처리하고 앱으로 넘기지 않음)
    public static final String TOKEN_RENEW_PUB_DESTINATION = "/pub/auth/token";

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
        // 개인 경로도 /sub/gameroomId/로 시작하므로 GAME_USER를 GAME보다 먼저 확인
        else if(destination.startsWith(GAME_SUB_PREFIX) && destination.contains(GAME_USER_SUB_INFIX))
            return WebSocketPurposeCode.GAME_USER;
        else if(destination.startsWith(GAME_SUB_PREFIX) || destination.startsWith(GAME_PUB_PREFIX))
            return WebSocketPurposeCode.GAME;
        // 게임방 목록 실시간 갱신
        else if(destination.equals(GAMEROOM_LIST_SUB_DESTINATION))
            return WebSocketPurposeCode.GAMEROOM_LIST;
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
            case CHAT -> extractRoomIdByCommand(destination, stompCommand, CHATROOM_SUB_PATTERN, CHATROOM_PUB_PATTERN);
            case GAME -> extractRoomIdByCommand(destination, stompCommand, GAME_SUB_PATTERN, GAME_PUB_PATTERN);
            case GAME_USER -> {

                if(stompCommand != StompCommand.SUBSCRIBE) {
                    CustomLogger.warn(
                            LogEventCode.WEBSOCKET,
                            "FAIL",
                            LogEventReasonCode.INVALID_WEBSOCKET_PREFIX,
                            LogEventReasonCode.INVALID_WEBSOCKET_PREFIX.getMessage()
                                    + " - destination: " + destination
                                    + " - COMMAND: " + stompCommand,
                            null
                    );

                    throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
                }

                yield getDestinationRoomIdIfDestAndPatternMatch(destination, GAME_USER_SUB_PATTERN);
            }
            // 게임방 목록: 방 id 없음, 구독만 가능
            case GAMEROOM_LIST -> {

                if(stompCommand != StompCommand.SUBSCRIBE) {
                    CustomLogger.warn(
                            LogEventCode.WEBSOCKET,
                            "FAIL",
                            LogEventReasonCode.INVALID_WEBSOCKET_PREFIX,
                            LogEventReasonCode.INVALID_WEBSOCKET_PREFIX.getMessage()
                                    + " - destination: " + destination
                                    + " - COMMAND: " + stompCommand,
                            null
                    );

                    throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
                }

                yield null;
            }
        };
    }

    // userUuid 추출
    public UUID extractUserUuid(String destination) {

        Matcher matcher = GAME_USER_SUB_PATTERN.matcher(destination);
        if(!matcher.matches()) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.DESTINATION_NOT_MATCH_WITH_PATTERN,
                    LogEventReasonCode.DESTINATION_NOT_MATCH_WITH_PATTERN.getMessage()
                            + " - destination: " + destination
                            + " - Pattern: " + GAME_USER_SUB_PATTERN,
                    null
            );

            throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
        }

        return UUID.fromString(matcher.group(2));
    }

    // sub, pub에 따라 패턴 맞춰 id 추출
    private Long extractRoomIdByCommand(String destination, StompCommand stompCommand,
                                    Pattern subPattern, Pattern pubPattern) {

        switch (stompCommand) {
            case SUBSCRIBE -> {
                return getDestinationRoomIdIfDestAndPatternMatch(destination, subPattern);
            }
            case SEND -> {
                return getDestinationRoomIdIfDestAndPatternMatch(destination, pubPattern);
            }
            default -> {

                CustomLogger.warn(
                        LogEventCode.WEBSOCKET,
                        "FAIL",
                        LogEventReasonCode.STOMP_COMMAND_NOT_REQUIRED_EXTRACT_ROOM_ID_METHOD,
                        LogEventReasonCode.STOMP_COMMAND_NOT_REQUIRED_EXTRACT_ROOM_ID_METHOD.getMessage()
                            + " - COMMAND: " + stompCommand,
                        null
                );

                throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
            }
        }
    }

    private Long getDestinationRoomIdIfDestAndPatternMatch(String destination, Pattern pattern) {

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
