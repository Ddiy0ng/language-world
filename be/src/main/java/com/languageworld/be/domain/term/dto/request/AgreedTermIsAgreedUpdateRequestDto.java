package com.languageworld.be.domain.term.dto.request;

import jakarta.validation.constraints.NotNull;

public record AgreedTermIsAgreedUpdateRequestDto(

        @NotNull
        boolean isTermAgreed
) {}
