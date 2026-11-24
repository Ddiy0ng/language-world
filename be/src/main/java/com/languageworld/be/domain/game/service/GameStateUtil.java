package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.entity.GameState;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// 서버 메모리에서 관리하는 게임방 상태들
@Component
public class GameStateUtil {

    // gameroomId → 게임방 상태
    private final Map<Long, GameState> gameStateMap = new ConcurrentHashMap<>();

    public void saveGameroomGameState(GameState gameState) {
        gameStateMap.put(gameState.getGameroomId(), gameState);
    }

    public Optional<GameState> findOptionalGameState(Long gameroomId) {
        return Optional.ofNullable(gameStateMap.get(gameroomId));
    }

    public void remove(Long gameroomId) {
        gameStateMap.remove(gameroomId);
    }

    // 자리가 남은 방(WAIT, STARTED 모두)
    public List<Long> findJoinableGameroomIdList() {
        return gameStateMap.values().stream()
                .filter(gameState -> !gameState.isFull())
                .map(GameState::getGameroomId)
                .toList();
    }

    // 사용자가 참여 중인 방: 모든 방을 돌며 참가자 Map에서 확인
    public Optional<Long> findGameroomIdByUserUuid(UUID userUuid) {
        return gameStateMap.values().stream()
                .filter(gameState -> gameState.contains(userUuid))
                .map(GameState::getGameroomId)
                .findFirst();
    }
}