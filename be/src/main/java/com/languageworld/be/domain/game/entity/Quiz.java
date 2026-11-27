package com.languageworld.be.domain.game.entity;

import lombok.Builder;
import lombok.Getter;

import java.text.Normalizer;

@Getter
@Builder
public class Quiz {

    public static final int CORRECT_SCORE = 10;

    private final Long vocabMeaningId;

    private final String answer;

    private int score;

    private boolean isSubmitted;

    public static Quiz of(Long vocabMeaningId, String answer) {

        return Quiz.builder()
                .vocabMeaningId(vocabMeaningId)
                .answer(answer)
                .score(CORRECT_SCORE)
                .isSubmitted(false)
                .build();
    }

    public boolean matches(String input) {

        if(input == null)
            return false;

        String normalizedInput = normalize(input);

        if(normalizedInput == null)
            return false;

        return normalizedInput.equalsIgnoreCase(normalize(answer));
    }

    public void submitted() {this.isSubmitted = true;}

    // 공백 제거, 정규화
    private String normalize(String input) {return Normalizer.normalize(input.strip(), Normalizer.Form.NFC);}
}