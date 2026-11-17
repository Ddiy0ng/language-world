package com.languageworld.be.domain.vocab.dto.response;

import com.languageworld.be.domain.vocab.entity.VocabMeaning;
import lombok.Builder;

@Builder
public record VocabMeaningResponseDto(

        Long vocabId,
        Long vocabMeaningId,
        String learningLanguage,
        String level,
        String learningLanguageMeaning
) {

    public static VocabMeaningResponseDto from(VocabMeaning vocabMeaning) {

        return VocabMeaningResponseDto.builder()
                .vocabId(vocabMeaning.getVocab().getId())
                .vocabMeaningId(vocabMeaning.getId())
                .learningLanguage(vocabMeaning.getLanguage().getType())
                .level(vocabMeaning.getLevel().getLevel())
                .learningLanguageMeaning(vocabMeaning.getMeaning())
                .build();
    }
}
