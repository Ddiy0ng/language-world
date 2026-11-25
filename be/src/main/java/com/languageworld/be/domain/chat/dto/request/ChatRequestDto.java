package com.languageworld.be.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatRequestDto(

        @NotBlank
        String chatMessage
) {
}
