package com.languageworld.be.domain.game.entity;

import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.global.baseEntity.CreatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Table(name = "game_results")
@Entity
@Getter
public class GameResult extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_rank", nullable = false)
    private int gameRank;

    @Column(nullable = false)
    private int score;

    @Column(name = "answer_size", nullable = false)
    private int answerSize;

    @ManyToOne
    @JoinColumn(name = "game_room_id", nullable = false)
    private GameRoom gameRoom;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;
}
