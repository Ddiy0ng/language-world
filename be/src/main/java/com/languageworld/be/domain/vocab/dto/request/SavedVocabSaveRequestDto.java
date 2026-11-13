package com.languageworld.be.domain.vocab.dto.request;

import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record SavedVocabSaveRequestDto (

        @Length(max = 300)
        String memo
){
}
