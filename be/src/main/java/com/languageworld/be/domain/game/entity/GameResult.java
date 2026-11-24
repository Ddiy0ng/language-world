package com.languageworld.be.domain.game.entity;

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

    @ManyToOne
    @JoinColumn(name = "game_room_id", nullable = false)
    private Gameroom gameRoom;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    public static GameResult of(UUID matchUuid, int gameRank, int score, int answerSize, Gameroom gameroom, User participant) {

        return GameResult.builder()
                .matchUuid(matchUuid)
                .gameRank(gameRank)
                .score(score)
                .answerSize(answerSize)
                .gameRoom(gameroom)
                .participant(participant)
                .build();
    }
}
