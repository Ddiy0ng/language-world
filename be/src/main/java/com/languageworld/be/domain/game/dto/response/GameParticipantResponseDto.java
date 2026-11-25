package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.Participant;
import lombok.Builder;

import java.util.UUID;

@Builder
public record GameParticipantResponseDto(

        UUID userUuid,
        String name,
        int score
) {

    public static GameParticipantResponseDto from(Participant participant) {

        return GameParticipantResponseDto.builder()
                .userUuid(participant.getUserUuid()).name(participant.getName()).score(participant.getScore())
                .build();
    }
}
