package com.languageworld.be.domain.vocab.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SavedVocabDeleteRequestDto(

        @NotEmpty
        List<@NotNull Long> savedVocabIdList
) {
}
