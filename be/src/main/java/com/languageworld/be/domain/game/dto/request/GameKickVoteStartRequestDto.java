package com.languageworld.be.domain.game.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GameKickVoteStartRequestDto(

        @NotNull
        UUID targetUuid
) {
}
