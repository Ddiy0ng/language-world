package com.languageworld.be.domain.game.entity;

import com.languageworld.be.domain.game.enumGroup.GameUserStatusCode;
import lombok.Getter;

@Getter
public class GameUser {

    private Long userId;

    private GameUserStatusCode status;

    private int score;
}
