package com.languageworld.be.domain.vocab.dto.request;

import org.hibernate.validator.constraints.Length;

public record SavedVocabUpdateRequestDto(

        @Length(max = 300)
        String memo
) {
}
