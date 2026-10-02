package com.languageworld.be.domain.game.entity;

import com.languageworld.be.domain.game.enumGroup.GameRoomStatusCode;
import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import java.time.LocalDateTime;

@Table(name = "game_rooms")
@Entity
@Getter
public class GameRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "max_player_size", nullable = false)
    @Min(2)
    @Max(8)
    private int maxPlayerSize;

    @Column(name = "round_size", nullable = false)
    @Min(8)
    @Max(20)
    private int roundSize;

    @Column(name = "is_locked", nullable = false)
    private boolean isLocked;

    private String password;

    @Column(name = "game_room_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameRoomStatusCode gameRoomStatus;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @ManyToOne
    @JoinColumn(name = "learning_language_id", nullable = false)
    private Language learningLanguage;

    @ManyToOne
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
