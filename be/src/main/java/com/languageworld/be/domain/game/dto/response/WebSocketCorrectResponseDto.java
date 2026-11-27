package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.entity.Participant;
import lombok.Builder;

import java.util.UUID;

@Builder
public record WebSocketCorrectResponseDto(

        UUID answererUuid,
        String answer        // 정답 공개
) {
    public static WebSocketCorrectResponseDto of(GameState gameState, Participant answerer) {

        return WebSocketCorrectResponseDto.builder()
                .answererUuid(answerer.getUserUuid())
                .answer(gameState.currentQuiz().getAnswer())
                .build();
    }
}