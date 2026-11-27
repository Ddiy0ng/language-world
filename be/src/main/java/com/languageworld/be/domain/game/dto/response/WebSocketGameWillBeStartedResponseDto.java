package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.GameState;
import lombok.Builder;

@Builder
public record WebSocketGameWillBeStartedResponseDto(

        int countdownTime
) {
    public static WebSocketGameWillBeStartedResponseDto from() {

        return WebSocketGameWillBeStartedResponseDto.builder()
                .countdownTime(GameState.COUNTDOWN_TIME)
                .build();
    }
}