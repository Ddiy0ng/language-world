package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.GameState;
import lombok.Builder;

import java.util.List;

@Builder
public record WebSocketMatchEndResponseDto(

        int playedRoundSize,      // 실제 진행된 라운드 수
        List<WebSocketGameResultResponseDto> webSocketGameResultResponseDtoList
) {
    // gameState.finishMatch() 전에 호출 (이후엔 라운드가 초기화됨)
    public static WebSocketMatchEndResponseDto from(GameState gameState, List<WebSocketGameResultResponseDto> webSocketGameResultResponseDtoList) {

        return WebSocketMatchEndResponseDto.builder()
                .playedRoundSize(gameState.getCurrentRound())
                .webSocketGameResultResponseDtoList(webSocketGameResultResponseDtoList)
                .build();
    }
}