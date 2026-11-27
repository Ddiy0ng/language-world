package com.languageworld.be.global.webSocket.enumGroup;

public enum WebSocketEventTypeCode {

    JOINED,
    LEFT,
    CHAT,
    GAME_WILL_BE_STARTED,
    ROUND_STARTED,
    DRAWER_QUIZ,
    DRAW_TURN,
    ANSWER_NOT_ALLOWED,
    CORRECT_ANSWER,
    ROUND_TIMEOUT,
    ROUND_SKIPPED,
    GAME_ENDED,
    KICK_VOTE_STARTED,
    KICK_VOTE_ENDED,

    // 게임방 목록(/sub/gameroomList)
    GAMEROOM_UPDATED,
    GAMEROOM_REMOVED

}
