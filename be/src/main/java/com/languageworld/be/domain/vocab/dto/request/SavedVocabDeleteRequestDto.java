package com.languageworld.be.domain.vocab.dto.request;

import java.util.List;

public record SavedVocabDeleteRequestDto(

        List<Long> savedVocabIdList
) {
}
