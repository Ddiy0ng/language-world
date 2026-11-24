package com.languageworld.be.domain.game.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GameroomCreateRequestDto(

        @Min(2)
        @Max(8)
        int maxPlayerSize,

        @Min(8)
        @Max(20)
        int roundSize,

        boolean isLocked,

        @Size(min = 1, max = 8)
        String password,

        @NotNull
        Long learningLanguageId,

        @NotNull
        Long levelId
) {
}
