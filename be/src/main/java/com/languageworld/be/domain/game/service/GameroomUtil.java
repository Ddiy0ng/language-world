package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.entity.Gameroom;
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
        if(optionalGameroom.isEmpty()) {
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
}
