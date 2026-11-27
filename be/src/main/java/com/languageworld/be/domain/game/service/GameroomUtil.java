package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.webSocket.service.WebSocketSessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GameroomUtil {

    private final GameStateUtil gameStateUtil;
    private final WebSocketSessionUtil webSocketSessionUtil;

    public GameState validGameroomMemberCheck(Long gameroomId, UUID userUuid) {

        GameState gameState = gameStateUtil.getGameStateById(gameroomId, userUuid);

        if(!gameState.contains(userUuid)) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.NOT_GAMEROOM_MEMBER,
                    LogEventReasonCode.NOT_GAMEROOM_MEMBER.getMessage()
                            + " - gameroomId: " + gameroomId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.NOT_GAMEROOM_MEMBER);
        }

        return gameState;
    }

    // 실시간 연결 확인: 연결이 끊길 때 자동 퇴장되므로, 연결 없이 들어오면 방에 계속 남게 됨
    public void webSocketConnectedCheck(UUID userUuid) {

        if(!webSocketSessionUtil.hasWebSocketSessionByUserUuid(userUuid)) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.GAME_WEBSOCKET_NOT_CONNECTED,
                    LogEventReasonCode.GAME_WEBSOCKET_NOT_CONNECTED.getMessage(),
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.GAME_WEBSOCKET_NOT_CONNECTED);
        }
    }
}
