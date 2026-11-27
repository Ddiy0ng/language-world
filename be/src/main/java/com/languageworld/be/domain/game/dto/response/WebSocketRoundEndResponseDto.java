package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.GameState;
import lombok.Builder;

@Builder
public record WebSocketRoundEndResponseDto(

        String answer         // 아무도 못 맞힌 정답 공개
) {
    public static WebSocketRoundEndResponseDto from(GameState gameState) {

        return WebSocketRoundEndResponseDto.builder()
                .answer(gameState.currentQuiz().getAnswer())
                .build();
    }
}