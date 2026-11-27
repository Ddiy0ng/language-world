package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.repository.GameStateManage;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GameStateUtil {

    private final GameStateManage gameStateManage;

    // 진행 중이고 같은 매치인 방 상태, 아니면 null(예약 사이에 종료·재시작·삭제됨)
    public GameState getPlayingGameState(Long gameroomId, UUID matchUuid) {

        return gameStateManage.findOptionalGameState(gameroomId)
                .filter(GameState::isPlaying)
                .filter(gameState -> matchUuid.equals(gameState.getMatchUuid()))
                .orElse(null);
    }

    public void saveGameroomGameState(GameState gameState) {

        Map<Long, GameState> gameStateMap = gameStateManage.getGameStateMap();
        gameStateMap.put(gameState.getGameroomId(), gameState);
    }

    public GameState getGameStateById(Long gameroomId, UUID userUuid) {

        Optional<GameState> optionalGameState = gameStateManage.findOptionalGameState(gameroomId);
        if(optionalGameState.isEmpty()) {
            CustomLogger.warn(LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.GAMEROOM_NOT_FOUND,
                    LogEventReasonCode.GAMEROOM_NOT_FOUND.getMessage() + " - (not in memory) gameroomId: " + gameroomId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.GAMEROOM_NOT_FOUND);
        }

        return optionalGameState.get();
    }

    public void gameroomEnterCheck(UUID userUuid) {

        if(gameStateManage.findGameroomIdByUserUuid(userUuid).isPresent()) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.ALREADY_IN_GAMEROOM,
                    LogEventReasonCode.ALREADY_IN_GAMEROOM.getMessage(),
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.ALREADY_IN_GAMEROOM);
        }
    }

    public void remove(Long gameroomId) {

        Map<Long, GameState> gameStateMap = gameStateManage.getGameStateMap();
        gameStateMap.remove(gameroomId);
    }
}
