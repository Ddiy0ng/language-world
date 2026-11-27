package com.languageworld.be.domain.game.entity;

import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.global.baseEntity.CreatableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Table(name = "game_results")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GameResult extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_Uuid", nullable = false)
    private UUID matchUuid;

    @Column(name = "game_rank", nullable = false)
    private int gameRank;

    @Column(nullable = false)
    private int score;

    @Column(name = "answer_size", nullable = false)
    private int answerSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_language_id", nullable = false)
    private Language learningLanguage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    public static GameResult of(UUID matchUuid, int gameRank, int score, int answerSize,
                                Language learningLanguage, Level level, User participant) {

        return GameResult.builder()
                .matchUuid(matchUuid)
                .gameRank(gameRank)
                .score(score)
                .answerSize(answerSize)
                .learningLanguage(learningLanguage)
                .level(level)
                .participant(participant)
                .build();
    }
}
