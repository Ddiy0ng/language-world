package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.entity.Gameroom;
import com.languageworld.be.domain.game.enumGroup.GameroomStatusCode;
import com.languageworld.be.domain.game.repository.GameroomRepository;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GameroomUtil {

    private final GameroomRepository gameroomRepository;
    private final GameStateUtil gameStateUtil;

    public Gameroom getGameroomById(Long gameroomId, UUID userUuid) {

        Optional<Gameroom> optionalGameroom = gameroomRepository.findById(gameroomId);
        if(optionalGameroom.isEmpty() || optionalGameroom.get().getGameroomStatus() == GameroomStatusCode.CLOSED) {
            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.GAMEROOM_NOT_FOUND,
                    LogEventReasonCode.GAMEROOM_NOT_FOUND.getMessage()
                            + " - gameroomId: " + gameroomId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.GAMEROOM_NOT_FOUND);
        }

        return optionalGameroom.get();
    }

    public GameState getGameStateById(Long gameroomId, UUID userUuid) {

        Optional<GameState> optionalGameState = gameStateUtil.findOptionalGameState(gameroomId);
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
}
