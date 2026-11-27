package com.languageworld.be.domain.game.dto.response;

import lombok.Builder;

@Builder
public record GameQuizResponseDto(

        int round,
        String answer
) {
    public static GameQuizResponseDto of(int round, String answer) {

        return GameQuizResponseDto.builder()
                .round(round).answer(answer)
                .build();
    }
}