package com.languageworld.be.domain.game.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Builder
@AllArgsConstructor
@Getter
public class GameState {

    private final Long gameroomId;

    private final int maxPlayerSize;

    private final int roundSize;

    private UUID hostUuid;

    private boolean playing;

    private UUID matchUuid;

    @Builder.Default
    private List<Quiz> quizList = List.of();

    private int currentRound;

    private ScheduledFuture<?> roundTimer;

    private boolean answered;

    private final Map<UUID, Participant> participantMap = new LinkedHashMap<>();

    private UUID drawerUuid;

    private long lastDrawerJoinSequence;

    private long nextJoinSequence;


    public static GameState of(Long gameroomId, int maxPlayerSize, int roundSize, UUID hostUuid) {

        return GameState.builder()
                .gameroomId(gameroomId)
                .maxPlayerSize(maxPlayerSize)
                .roundSize(roundSize)
                .hostUuid(hostUuid)
                .build();
    }

    public void addParticipant(UUID userUuid, String name) {
        participantMap.put(userUuid, Participant.of(userUuid, name, nextJoinSequence++));
    }

    public Participant removeParticipant(UUID userUuid) {
        return participantMap.remove(userUuid);
    }

    public boolean contains(UUID userUuid) {
        return participantMap.containsKey(userUuid);
    }

    public boolean isFull() {
        return participantMap.size() >= maxPlayerSize;
    }

    public int getParticipantSize(){return participantMap.size();}

    // 방장 위임 대상: 가장 먼저 들어온 사람
    public UUID getOldestParticipantUuid() {
        return participantMap.keySet().iterator().next();
    }

    public void changeHost(UUID hostUuid) {
        this.hostUuid = hostUuid;
    }

    // 게임 시작
    public void startMatch(UUID matchUuid, List<Quiz> quizList) {
        this.playing = true;
        this.matchUuid = matchUuid;
        this.quizList = List.copyOf(quizList);
        this.currentRound = 0;
        this.drawerUuid = null;
        this.lastDrawerJoinSequence = -1;
        this.answered = false;
        participantMap.values().forEach(Participant::resetScore);
    }

    public Quiz currentQuiz() {
        return quizList.get(currentRound - 1);
    }

    public boolean hasNextRound() {
        return currentRound < quizList.size();
    }

    // 다음 라운드: 직전에 그린 사람보다 입장 순번이 큰 첫 사람, 없으면 맨 앞 사람
    public void moveToNextRound() {
        currentRound++;
        answered = false;

        Participant nextDrawer = participantMap.values().stream()
                .filter(p -> p.getJoinSequence() > lastDrawerJoinSequence)
                .findFirst()
                .orElseGet(() -> participantMap.values().iterator().next());

        drawerUuid = nextDrawer.getUserUuid();
        lastDrawerJoinSequence = nextDrawer.getJoinSequence();
    }

    public boolean isDrawer(UUID userUuid) {
        return userUuid.equals(drawerUuid);
    }

    public void markAnswered() {
        this.answered = true;
    }

    public void replaceRoundTimer(ScheduledFuture<?> newTimer) {
        cancelRoundTimer();
        this.roundTimer = newTimer;
    }

    public void cancelRoundTimer() {
        if (roundTimer != null) roundTimer.cancel(false);
        roundTimer = null;
    }

    public void finishMatch() {
        cancelRoundTimer();
        playing = false;
        matchUuid = null;
        quizList = List.of();
        currentRound = 0;
        drawerUuid = null;
        answered = false;
    }
}