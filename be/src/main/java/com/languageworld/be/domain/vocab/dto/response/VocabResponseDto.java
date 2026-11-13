package com.languageworld.be.domain.vocab.dto.response;

import com.languageworld.be.domain.vocab.entity.VocabMeaning;
import lombok.Builder;

@Builder
public record VocabResponseDto(

        Long vocabId,
        Long vocabMeaningId,
        String learningLanguage,
        String level,
        String learningLanguageMeaning,
        String motherTongueMeaning
) {

    public static VocabResponseDto from(VocabMeaning vocabMeaning, String motherTongueMeaning) {

        return VocabResponseDto.builder()
                .vocabId(vocabMeaning.getVocab().getId())
                .vocabMeaningId(vocabMeaning.getId())
                .learningLanguage(vocabMeaning.getLanguage().getType())
                .level(vocabMeaning.getLevel().getLevel())
                .learningLanguageMeaning(vocabMeaning.getMeaning())
                .motherTongueMeaning(motherTongueMeaning)
                .build();
    }
}
