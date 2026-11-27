package com.languageworld.be.domain.game.dto.response;

import lombok.Builder;

@Builder
public record WebSocketGameDrawResponseDto(

        String drawData
) {
    public static WebSocketGameDrawResponseDto from(String drawData) {

        return WebSocketGameDrawResponseDto.builder()
                .drawData(drawData)
                .build();
    }
}
