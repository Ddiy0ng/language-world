package com.languageworld.be.domain.vocab.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record SavedVocabSaveRequestDto(

        @NotNull
        Long vocabMeaningId,

        @Length(max = 300)
        String memo
) {
}
