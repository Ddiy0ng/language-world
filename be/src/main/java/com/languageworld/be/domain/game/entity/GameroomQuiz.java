package com.languageworld.be.domain.game.entity;

import com.languageworld.be.domain.vocab.entity.VocabMeaning;
import com.languageworld.be.global.baseEntity.CreatableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Table(name = "gameroom_quizs")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameroomQuiz extends CreatableEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_uuid", nullable = false)
    private UUID matchUuid;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @Builder.Default
    @Column(name = "is_revealed", nullable = false)
    private boolean isRevealed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_room_id", nullable = false)
    private Gameroom gameroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocab_meaning_id", nullable = false)
    private VocabMeaning vocabMeaning;

    public static GameroomQuiz of(UUID matchUuid, int roundNumber, Gameroom gameroom, VocabMeaning vocabMeaning) {
        return GameroomQuiz.builder()
                .matchUuid(matchUuid)
                .roundNumber(roundNumber)
                .gameroom(gameroom)
                .vocabMeaning(vocabMeaning)
                .build();
    }
}