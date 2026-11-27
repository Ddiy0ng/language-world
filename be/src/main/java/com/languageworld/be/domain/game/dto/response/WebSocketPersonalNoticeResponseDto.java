package com.languageworld.be.domain.game.dto.response;

import lombok.Builder;

@Builder
public record WebSocketPersonalNoticeResponseDto(

        String message
) {

    public static WebSocketPersonalNoticeResponseDto from(String message) {

        return WebSocketPersonalNoticeResponseDto.builder()
                .message(message)
                .build();
    }
}
