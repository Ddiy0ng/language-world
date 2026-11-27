package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.Participant;
import lombok.Builder;

import java.util.UUID;

@Builder
public record WebSocketGameChatResponseDto(

        UUID senderUuid,
        String chatMessage
) {
    public static WebSocketGameChatResponseDto of(Participant sender, String chatMessage) {

        return WebSocketGameChatResponseDto.builder()
                .senderUuid(sender.getUserUuid())
                .chatMessage(chatMessage)
                .build();
    }
}
