package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.GameState;
import lombok.Builder;

import java.util.UUID;

@Builder
public record WebSocketRoundStartResponseDto(

        int round,
        UUID drawerUuid,      // 내 uuid와 같으면 GET /gamerooms/{id}/quiz 로 단어 확인
        int roundTime
) {
    public static WebSocketRoundStartResponseDto from(GameState gameState) {

        return WebSocketRoundStartResponseDto.builder()
                .round(gameState.getCurrentRound())
                .drawerUuid(gameState.getDrawerUuid())
                .roundTime(gameState.getRoundTime())
                .build();
    }
}