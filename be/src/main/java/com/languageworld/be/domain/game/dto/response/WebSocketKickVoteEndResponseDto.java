package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.GameState;
import lombok.Builder;

import java.util.UUID;

// KICK_VOTE_ENDED: kicked가 true면 이어서 대상의 LEFT가 옴
@Builder
public record WebSocketKickVoteEndResponseDto(

        UUID targetUuid,
        boolean kicked,
        int agreeSize,
        int disagreeSize
) {
    // gameState.clearKickVote() 전에 호출
    public static WebSocketKickVoteEndResponseDto of(GameState gameState, boolean kicked) {

        return WebSocketKickVoteEndResponseDto.builder()
                .targetUuid(gameState.getKickVoteTargetUuid())
                .kicked(kicked)
                .agreeSize(gameState.getKickAgreeSize())
                .disagreeSize(gameState.getKickDisagreeSize())
                .build();
    }
}
