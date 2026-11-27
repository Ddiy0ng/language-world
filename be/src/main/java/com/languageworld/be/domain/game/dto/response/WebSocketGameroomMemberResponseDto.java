package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.GameState;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record WebSocketGameroomMemberResponseDto(

        //JOIN, LEFT
        UUID targetUuid,
        String targetName,
        UUID hostUuid,
        List<GameParticipantResponseDto> participantList   // 입장 순서
) {
    public static WebSocketGameroomMemberResponseDto from(GameState gameState, UUID targetUuid, String targetName) {

        return WebSocketGameroomMemberResponseDto.builder()
                .targetUuid(targetUuid)
                .targetName(targetName)
                .hostUuid(gameState.getHostUuid())
                .participantList(gameState.getParticipantMap().values().stream()
                        .map(GameParticipantResponseDto::from)
                        .toList())
                .build();
    }
}
