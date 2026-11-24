package com.languageworld.be.domain.game.dto.request;

import jakarta.validation.constraints.Size;

public record GameroomEnterRequestDto(

        @Size(min = 1, max = 8)
        String password
) {
}
