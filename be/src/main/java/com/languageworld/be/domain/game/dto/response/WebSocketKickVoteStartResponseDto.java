package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.GameState;
import lombok.Builder;

import java.util.UUID;

// KICK_VOTE_STARTED
@Builder
public record WebSocketKickVoteStartResponseDto(

        UUID targetUuid,     // 추방 대상
        String targetName,
        int voterSize,       // 투표할 수 있는 사람 수(대상 제외), 찬성이 이 수의 절반을 넘으면 추방
        int voteTime         // 투표 제한 시간(초)
) {
    public static WebSocketKickVoteStartResponseDto of(GameState gameState, String targetName) {

        return WebSocketKickVoteStartResponseDto.builder()
                .targetUuid(gameState.getKickVoteTargetUuid())
                .targetName(targetName)
                .voterSize(gameState.getKickVoterSize())
                .voteTime(GameState.KICK_VOTE_TIME)
                .build();
    }
}
