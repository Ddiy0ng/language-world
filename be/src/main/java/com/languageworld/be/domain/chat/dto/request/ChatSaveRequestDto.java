package com.languageworld.be.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatSaveRequestDto(

        @NotBlank
        String chatMessage
) {
}
