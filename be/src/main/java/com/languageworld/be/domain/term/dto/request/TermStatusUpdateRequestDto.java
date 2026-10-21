package com.languageworld.be.domain.term.dto.request;

import jakarta.validation.constraints.NotNull;

public record TermStatusUpdateRequestDto(

        @NotNull
        boolean changeIntoStatus
) {}
