package com.languageworld.be.domain.term.dto.request;

import com.languageworld.be.domain.term.enumGroup.TermPurposeCode;
import jakarta.validation.constraints.NotNull;

public record TermCreateRequestDto(

        @NotNull
        TermPurposeCode purpose
) {}