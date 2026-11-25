package com.languageworld.be.domain.game.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GameChatRequestDto(

        @NotBlank
        String chatMessage
) {
}
