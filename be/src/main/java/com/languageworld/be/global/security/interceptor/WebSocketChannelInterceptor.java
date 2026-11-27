package com.languageworld.be.global.security.interceptor;

import com.languageworld.be.domain.chat.service.ChatroomUserUtil;
import com.languageworld.be.domain.chat.service.ChatroomUtil;
import com.languageworld.be.domain.game.service.GameroomUtil;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.jwt.JwtUtil;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import com.languageworld.be.global.webSocket.entity.CustomWebSocketSession;
import com.languageworld.be.global.webSocket.enumGroup.WebSocketPurposeCode;
import com.languageworld.be.global.webSocket.service.WebSocketSessionUtil;
import com.languageworld.be.global.webSocket.service.WebSocketUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final WebSocketUtil webSocketUtil;
    private final WebSocketSessionUtil webSocketSessionUtil;
    private final UserUtil userUtil;
    private final ChatroomUtil chatroomUtil;
    private final ChatroomUserUtil chatroomUserUtil;
    private final GameroomUtil gameroomUtil;

    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        // STOMP Header 검사
        StompHeaderAccessor stompHeaderAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(stompHeaderAccessor == null) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.STOMP_HEADER_ACCESSOR_IS_NULL,
                    LogEventReasonCode.STOMP_HEADER_ACCESSOR_IS_NULL.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.INTERNAL_SERVER_ERROR);
        }

        // Command 검사
        StompCommand stompCommand = stompHeaderAccessor.getCommand();
        if(stompCommand == null) {

            // HEARTBEAT 통과, 외에는 예외
            if(stompHeaderAccessor.isHeartbeat())
                return message;

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.STOMP_COMMAND_IS_NULL,
                    LogEventReasonCode.STOMP_COMMAND_IS_NULL.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
        }

        switch (stompCommand) {
            case CONNECT-> {
                // auth header
                authenticateByStompHeaderAccessor(stompHeaderAccessor);

            }
            case SUBSCRIBE-> {

                // 인증 사용자인지 확인
                UUID userUuid = getAuthenticatedUserUuid(stompHeaderAccessor);

                // 탈퇴 회원 검사
                userUtil.deletedUserCheckByUserUuid(userUuid);

                // destination 검증 (SUBSCRIBE용 경로: 수신)
                String destination = webSocketUtil.getDestination(stompHeaderAccessor);
                WebSocketPurposeCode webSocketPurposeCode = webSocketUtil.getWebSocketPurpose(destination);
                Long roomId = webSocketUtil.extractRoomId(webSocketPurposeCode, destination, stompCommand);

                // 목적에 따른 개별 검증
                switch (webSocketPurposeCode) {
                    case CHAT -> {
                        // 채팅방 존재 확인
                        chatroomUtil.getChatroomById(roomId, userUuid);

                        // 유효한 사용자: 채팅방 멤버 + 회원 탈퇴x + 퇴장 상태x + PERSONAL일 경우 차단 X
                        chatroomUserUtil.validChatroomUserCheck(roomId, userUuid);
                    }
                    case GAME -> {
                        gameroomUtil.validGameroomMemberCheck(roomId, userUuid);
                    }
                    case GAME_USER -> {

                        UUID personalSubUuid = webSocketUtil.extractUserUuid(destination);
                        if(!personalSubUuid.equals(userUuid)) {

                            CustomLogger.warn(
                                    LogEventCode.WEBSOCKET,
                                    "FAIL",
                                    LogEventReasonCode.NOT_USER_DESTINATION_OWNER,
                                    LogEventReasonCode.NOT_USER_DESTINATION_OWNER.getMessage()
                                            + " - destination: " + destination,
                                    userUuid.toString()
                            );

                            throw new CustomException(CustomExceptionCode.ACCESS_DENIED);
                        }
                        gameroomUtil.validGameroomMemberCheck(roomId, userUuid);
                    }
                    // 게임방 목록: 인증만 확인
                    case GAMEROOM_LIST -> {}
                }
            }
            case SEND-> {

                String destination = webSocketUtil.getDestination(stompHeaderAccessor);

                // 토큰 갱신: 연결은 그대로 두고 세션 인증만 새 토큰으로 바꿈, 앱으로 넘기지 않음(null 반환)
                if(WebSocketUtil.TOKEN_RENEW_PUB_DESTINATION.equals(destination)) {
                    renewByStompHeaderAccessor(stompHeaderAccessor);
                    return null;
                }

                // 인증 사용자인지 확인(탈퇴 여부는 CONNECT·SUBSCRIBE에서 확인, 그림처럼 자주 오는 SEND마다 DB 조회하지 않음)
                UUID userUuid = getAuthenticatedUserUuid(stompHeaderAccessor);

                // destination 검증 (SEND용 경로: 송신)
                WebSocketPurposeCode webSocketPurposeCode = webSocketUtil.getWebSocketPurpose(destination);
                Long roomId = webSocketUtil.extractRoomId(webSocketPurposeCode, destination, stompCommand);

                switch (webSocketPurposeCode) {
                    case CHAT -> {
                        // 채팅방 존재 확인
                        chatroomUtil.getChatroomById(roomId, userUuid);

                        // 유효한 사용자: 채팅방 멤버 + 회원 탈퇴x + 퇴장 상태x + PERSONAL일 경우 차단 X
                        chatroomUserUtil.validChatroomUserCheck(roomId, userUuid);
                    }
                    case GAME -> {
                        gameroomUtil.validGameroomMemberCheck(roomId, userUuid);
                    }
                }
            }
            case UNSUBSCRIBE -> {}
            case DISCONNECT -> {
                webSocketSessionUtil.removeWebSocketSession(stompHeaderAccessor.getSessionId());
            }
            default-> {

                CustomLogger.warn(
                        LogEventCode.WEBSOCKET,
                        "FAIL",
                        LogEventReasonCode.UNSUPPORTED_STOMP_COMMAND,
                        LogEventReasonCode.UNSUPPORTED_STOMP_COMMAND.getMessage(),
                        null
                );

                throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
            }
        }

        return message;
    }

    // 인증 객체
    private Authentication getAuthenticationFromStompHeaderAccessor(StompHeaderAccessor stompHeaderAccessor) {

        Authentication authorization = (Authentication)stompHeaderAccessor.getUser();

        if (authorization == null || !(authorization.getPrincipal() instanceof CustomPrincipal) || !authorization.isAuthenticated()) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.UNAUTHORIZED,
                    LogEventReasonCode.UNAUTHORIZED.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.UNAUTHORIZED);
        }

        return authorization;
    }

    // 커스텀 인증 객체
    private CustomPrincipal getCustomPrincipalFromAuthentication(Authentication authentication) {

        if (!(authentication.getPrincipal() instanceof CustomPrincipal customPrincipal)) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.UNAUTHORIZED,
                    LogEventReasonCode.UNAUTHORIZED.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.UNAUTHORIZED);
        }

        return customPrincipal;
    }

    // Authorization 헤더
    private String getAuthorizationHeaderFromStompHeaderAccessor(StompHeaderAccessor stompHeaderAccessor) {

        String authorizationHeader = stompHeaderAccessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.AUTH_HEADER_IS_NULL_OR_BLANK,
                    LogEventReasonCode.AUTH_HEADER_IS_NULL_OR_BLANK.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.UNAUTHORIZED);
        }

        return authorizationHeader;
    }

    // 첫 인증
    private void authenticateByStompHeaderAccessor(StompHeaderAccessor stompHeaderAccessor) {

        String authorizationHeader = getAuthorizationHeaderFromStompHeaderAccessor(stompHeaderAccessor);
        String token = jwtUtil.resolveToken(authorizationHeader);
        Claims claims = jwtUtil.getValidateClaimsFromToken(JwtUtil.ACCESS, token);
        Authentication authentication = jwtUtil.createAuthentication(claims);
        CustomPrincipal customPrincipal = getCustomPrincipalFromAuthentication(authentication);

        //탈퇴 확인
        userUtil.deletedUserCheckByUserUuid(customPrincipal.uuid());

        // 커스텀 웹소켓 세션
        CustomWebSocketSession customWebSocketSession = CustomWebSocketSession.of(customPrincipal, claims.getExpiration());

        // 커스텀 웹소켓 세션 등록
        webSocketSessionUtil.registerWebSocketSession(stompHeaderAccessor.getSessionId(), customWebSocketSession);

        // 인증 객체 등록
        stompHeaderAccessor.setUser(authentication);
    }

    // 인증 갱신: 같은 사용자의 새 access 토큰이면 세션 만료 시각을 바꿈
    private void renewByStompHeaderAccessor(StompHeaderAccessor stompHeaderAccessor) {

        String authorizationHeader = getAuthorizationHeaderFromStompHeaderAccessor(stompHeaderAccessor);
        String token = jwtUtil.resolveToken(authorizationHeader);
        Claims claims = jwtUtil.getValidateClaimsFromToken(JwtUtil.ACCESS, token);
        Authentication authentication = jwtUtil.createAuthentication(claims);
        CustomPrincipal customPrincipal = getCustomPrincipalFromAuthentication(authentication);

        //탈퇴 확인
        userUtil.deletedUserCheckByUserUuid(customPrincipal.uuid());

        // 등록된 세션의 사용자와 같을 때만 갱신
        webSocketSessionUtil.renewWebSocketSession(stompHeaderAccessor.getSessionId(), CustomWebSocketSession.of(customPrincipal, claims.getExpiration()));

        CustomLogger.info(
                LogEventCode.WEBSOCKET,
                "SUCCESS",
                SuccessCode.WEBSOCKET_SESSION_RENEW_SUCCESS.getMessage(),
                customPrincipal.uuid().toString()
        );
    }

    // 인증 확인
    private UUID getAuthenticatedUserUuid(StompHeaderAccessor stompHeaderAccessor) {

        // 웹소켓 세션 등록 확인
        CustomWebSocketSession customWebSocketSession = webSocketSessionUtil.getValidWebSocketSession(stompHeaderAccessor.getSessionId());

        // 인증 사용자
        Authentication authentication = getAuthenticationFromStompHeaderAccessor(stompHeaderAccessor);
        CustomPrincipal customPrincipal = getCustomPrincipalFromAuthentication(authentication);

        // 인증 사용자의 uuid
        UUID userUuid = customPrincipal.uuid();

        if(!customWebSocketSession.customPrincipal().uuid().equals(userUuid)) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.UNAUTHORIZED,
                    LogEventReasonCode.UNAUTHORIZED.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.UNAUTHORIZED);
        }

        return userUuid;
    }
}
