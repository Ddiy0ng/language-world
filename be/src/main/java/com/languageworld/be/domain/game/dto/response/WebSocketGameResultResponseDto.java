package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.entity.Participant;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Builder
public record WebSocketGameResultResponseDto(

        int rank,
        UUID userUuid,
        String name,
        int score
) {
    // 점수 높은 순, 같은 점수는 같은 순위, 다음 순위는 이어짐(100, 100, 80 → 1, 1, 2)
    public static List<WebSocketGameResultResponseDto> listFrom(GameState gameState) {

        List<Participant> sortedParticipantList = gameState.getParticipantMap().values().stream()
                .sorted(Comparator.comparingInt(Participant::getScore).reversed())
                .toList();

        List<WebSocketGameResultResponseDto> rankList = new ArrayList<>(sortedParticipantList.size());
        int rank = 0;
        Integer previousScore = null;
        for(Participant participant : sortedParticipantList) {

            if(previousScore == null || participant.getScore() != previousScore) {
                rank++;
                previousScore = participant.getScore();
            }

            rankList.add(WebSocketGameResultResponseDto.builder()
                    .rank(rank)
                    .userUuid(participant.getUserUuid())
                    .name(participant.getName())
                    .score(participant.getScore())
                    .build());
        }

        return rankList;
    }
}