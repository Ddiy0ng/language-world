package com.languageworld.be.domain.game.dto.request;

import jakarta.validation.constraints.NotNull;

public record GameKickVoteRequestDto(

        @NotNull
        Boolean agree
) {
}
