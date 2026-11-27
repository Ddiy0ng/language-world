package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.enumGroup.GameroomStatusCode;
import lombok.Builder;

@Builder
public record GameroomResponseDto(

        Long gameroomId,
        String hostName,
        String learningLanguage,
        String level,
        boolean isLocked,
        GameroomStatusCode gameroomStatus,
        int currentPlayerSize,
        int maxPlayerSize,
        int roundSize
) {

    public static GameroomResponseDto from(GameState gameState) {

        return GameroomResponseDto.builder()
                .gameroomId(gameState.getGameroomId())
                .hostName(gameState.getHostName())
                .learningLanguage(gameState.getLearningLanguageType())
                .level(gameState.getLevelName())
                .isLocked(gameState.isLocked())
                .gameroomStatus(gameState.isPlaying() ? GameroomStatusCode.STARTED : GameroomStatusCode.WAIT)
                .currentPlayerSize(gameState.getParticipantSize())
                .maxPlayerSize(gameState.getMaxPlayerSize())
                .roundSize(gameState.getRoundSize())
                .build();
    }
}
