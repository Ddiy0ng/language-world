package com.languageworld.be.domain.game.entity;

import com.languageworld.be.domain.game.dto.request.GameroomCreateRequestDto;
import com.languageworld.be.domain.game.enumGroup.GameroomStatusCode;
import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.global.baseEntity.CreatableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Table(name = "game_rooms")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Gameroom extends CreatableEntity {

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
    private GameroomStatusCode gameroomStatus;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @ManyToOne
    @JoinColumn(name = "learning_language_id", nullable = false)
    private Language learningLanguage;

    @ManyToOne
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    public static Gameroom of(GameroomCreateRequestDto gameroomCreateRequestDto, String encodedPassword, Language learningLanguage, Level level, User host) {
        return Gameroom.builder()
                .maxPlayerSize(gameroomCreateRequestDto.maxPlayerSize())
                .roundSize(gameroomCreateRequestDto.roundSize())
                .isLocked(gameroomCreateRequestDto.isLocked())
                .password(encodedPassword)
                .gameroomStatus(GameroomStatusCode.WAIT)
                .host(host)
                .learningLanguage(learningLanguage)
                .level(level)
                .build();
    }

    public void changeStatus(GameroomStatusCode gameroomStatusCode){ this.gameroomStatus = gameroomStatusCode;}

    public void changeHost(User host) {
        this.host = host;
    }
}
