package com.languageworld.be.domain.vocab.dto.response;

import com.languageworld.be.domain.vocab.entity.SavedVocab;
import lombok.Builder;

@Builder
public record SavedVocabResponseDto(

        Long savedVocabId,
        VocabMeaningResponseDto vocabMeaningResponseDto,
        String memo
) {

    public static SavedVocabResponseDto from(SavedVocab savedVocab) {

        return SavedVocabResponseDto.builder()
                .savedVocabId(savedVocab.getId())
                .vocabMeaningResponseDto(VocabMeaningResponseDto.from(savedVocab.getVocabMeaning()))
                .memo(savedVocab.getMemo())
                .build();
    }
}
