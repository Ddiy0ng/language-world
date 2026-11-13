package com.languageworld.be.domain.vocab.dto.response;

import com.languageworld.be.domain.vocab.entity.SavedVocab;
import lombok.Builder;

@Builder
public record SavedVocabResponseDto(

        Long savedVocabId,
        VocabResponseDto vocabResponseDto,
        String memo
) {

    public static SavedVocabResponseDto from(SavedVocab savedVocab, String motherTongueMeaning, String memo) {

        return SavedVocabResponseDto.builder()
                .savedVocabId(savedVocab.getId())
                .vocabResponseDto(VocabResponseDto.from(savedVocab.getVocabMeaning(), motherTongueMeaning))
                .memo(memo)
                .build();
    }
}
