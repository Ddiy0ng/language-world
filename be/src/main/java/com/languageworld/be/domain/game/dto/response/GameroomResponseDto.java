package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.Gameroom;
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
    public static GameroomResponseDto of(Gameroom gameroom, int currentPlayerSize) {

        return GameroomResponseDto.builder()
                .gameroomId(gameroom.getId())
                .hostName(gameroom.getHost().getName())
                .learningLanguage(gameroom.getLearningLanguage().getType())
                .level(gameroom.getLevel().getLevel())
                .isLocked(gameroom.isLocked())
                .gameroomStatus(gameroom.getGameroomStatus())
                .currentPlayerSize(currentPlayerSize)
                .maxPlayerSize(gameroom.getMaxPlayerSize())
                .roundSize(gameroom.getRoundSize())
                .build();
    }
}
