package com.languageworld.be.domain.game.dto.response;

import com.languageworld.be.domain.game.entity.Participant;
import lombok.Builder;

import java.util.UUID;

@Builder
public record GameChatResponseDto(

        UUID senderUuid,
        String senderName,
        String chatMessage
) {
    public static GameChatResponseDto of(Participant sender, String chatMessage) {

        return GameChatResponseDto.builder()
                .senderUuid(sender.getUserUuid())
                .senderName(sender.getName())
                .chatMessage(chatMessage)
                .build();
    }
}
