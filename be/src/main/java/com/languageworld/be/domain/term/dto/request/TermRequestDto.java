package com.languageworld.be.domain.term.dto.request;

import com.languageworld.be.domain.term.enumGroup.TermPurposeCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class TermRequestDto {

    public record TermCreate(

            @Schema(
                    name = "purpose",
                    example = "SERVICE_USE")
            @NotNull
            TermPurposeCode purpose
    ) {}

    public record TermStatusUpdate(

            boolean changeIntoStatus
    ) {}

    public record AgreedTermIsAgreedUpdate(

            boolean isTermAgreed
    ) {}
}
