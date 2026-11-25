package com.languageworld.be.domain.game.dto.response;

import lombok.Builder;

@Builder
public record GameroomCreateResponseDto(

        Long gameroomId
) {

    public static GameroomCreateResponseDto from(Long gameroomId) {
        return GameroomCreateResponseDto.builder()
                .gameroomId(gameroomId)
                .build();
    }
}