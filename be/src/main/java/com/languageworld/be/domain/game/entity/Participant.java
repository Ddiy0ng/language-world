package com.languageworld.be.domain.game.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
public class Participant {

    private final UUID userUuid;
    private final String name;
    private final long joinSequence;
    private int score;
    private int answerSize;

    public static Participant of(UUID userUuid, String name, long joinSequence) {

        return Participant.builder()
                .userUuid(userUuid).name(name).joinSequence(joinSequence)
                .build();
    }

    public void resetScore() {
        this.score = 0;
        this.answerSize = 0;
    }

    public void addScore(int score) {
        this.score += score;
        this.answerSize++;
    }
}