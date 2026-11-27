package com.languageworld.be.domain.game.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GameChatRequestDto(

        @NotBlank
        @Size(max = 200)
        String chatMessage
) {
}
