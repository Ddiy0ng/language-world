package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.GameState;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record WebSocketGameroomMemberResponseDto(

        Long gameroomId,
        UUID targetUuid,        // 들어오거나 나간 사람
        UUID hostUuid,
        List<GameParticipantResponseDto> participantList   // 입장 순서
) {
    public static WebSocketGameroomMemberResponseDto from(GameState gameState, UUID changedUserUuid) {

        return WebSocketGameroomMemberResponseDto.builder()
                .gameroomId(gameState.getGameroomId())
                .targetUuid(changedUserUuid)
                .hostUuid(gameState.getHostUuid())
                .participantList(gameState.getParticipantMap().values().stream()
                        .map(GameParticipantResponseDto::from)
                        .toList())
                .build();
    }
}
