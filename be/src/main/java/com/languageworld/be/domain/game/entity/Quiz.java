package com.languageworld.be.domain.game.entity;

import java.text.Normalizer;

public record Quiz (

        Long vocabMeaningId,

        String answer,

        int score
){

    public boolean matches(String input) {

        if(input == null)
            return false;

        String normalizedInput = normalize(input);

        if(normalizedInput == null)
            return false;

        return normalizedInput.equalsIgnoreCase(normalize(answer));
    }

    private String normalize(String input) {

        // 공백 제거, 정규화
        return Normalizer.normalize(input.strip(), Normalizer.Form.NFC);
    }
}