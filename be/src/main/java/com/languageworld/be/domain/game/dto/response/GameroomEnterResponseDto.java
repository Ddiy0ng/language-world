package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.GameState;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record GameroomEnterResponseDto(

        UUID hostUuid,
        List<GameParticipantResponseDto> participantList,   // 입장 순서
        int roundSize,
        int roundTime,

        // 진행 상태: 매치 중이 아니면 currentRound 0, drawerUuid null, remainingTime 0
        int currentRound,
        UUID drawerUuid,
        int remainingTime,
        int countdownTime,        // 시작 예고 중이면 남은 카운트다운(초), 아니면 0
        int nextMatchWaitTime,    // 다음 매치 대기 중이면 남은 시간(초), 아니면 0
        List<String> drawDataList   // 이번 라운드에 지금까지 그려진 그림(받은 순서), 라운드 진행 중이 아니면 빈 목록
) {
    public static GameroomEnterResponseDto from(GameState gameState) {

        return GameroomEnterResponseDto.builder()
                .hostUuid(gameState.getHostUuid())
                .participantList(gameState.getParticipantMap().values().stream()
                        .map(GameParticipantResponseDto::from)
                        .toList())
                .roundSize(gameState.getRoundSize())
                .roundTime(gameState.getRoundTime())
                .currentRound(gameState.getCurrentRound())
                .drawerUuid(gameState.getDrawerUuid())
                .remainingTime(gameState.getRemainingTime())
                .countdownTime(gameState.getCountdownRemainingTime())
                .nextMatchWaitTime(gameState.getNextMatchRemainingTime())
                .drawDataList(gameState.getDrawDataSnapshot())
                .build();
    }
}