package com.languageworld.be.domain.term.dto;

import com.languageworld.be.domain.term.enumGroup.TermPurposeCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TermCreateRequestDto(

        @Schema(
                name = "약관 목적",
                example = "SERVICE_USE")
        @NotNull
        TermPurposeCode purpose
) { }
