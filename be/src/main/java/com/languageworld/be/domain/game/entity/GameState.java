package com.languageworld.be.domain.game.entity;

import com.languageworld.be.domain.game.dto.request.GameroomCreateRequestDto;
import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Builder
@AllArgsConstructor
@Getter
public class GameState {

    public static final int COUNTDOWN_TIME = 3;
    public static final int ROUND_INTERVAL_TIME = 3;
    public static final int NEXT_MATCH_WAIT_TIME = 10;
    public static final int KICK_VOTE_TIME = 15;

    /**
     * 게임 상태 소유 게임방 정보
     **/
    private final Long gameroomId;

    private final int maxPlayerSize;

    private final int roundSize;

    private final int roundTime;

    private final boolean isLocked;

    private final String encodedPassword;

    // 결과 저장용 id, 방 목록 표시용 이름
    private final Long learningLanguageId;

    private final String learningLanguageType;

    private final Long levelId;

    private final String levelName;

    private UUID hostUuid;

    /**
     * 출제 후보(방 단위)
     **/
    // 섞인 id 목록
    private List<Long> quizIdPool;

    private int quizCursor;

    /**
     * 한 게임에 따라 변동되는 정보
     **/
    private boolean playing;

    private boolean waitingNextMatch;

    private UUID matchUuid;

    @Builder.Default
    private List<Quiz> quizList = List.of();

    // 시작 예고(카운트다운)가 끝나는 시각(ms), 시작 예고 중 입장자의 남은 카운트다운 계산용
    private long countdownEndAt;

    // 다음 매치가 시작되는 시각(ms), 다음 매치 대기 중 입장자의 남은 대기 시간 계산용
    private long nextMatchStartAt;

    /**
     * 한 문제에 따라 변동되는 정보
     **/
    private int currentRound;

    private ScheduledFuture<?> roundTimer;

    private boolean answered;

    // 라운드 시작 시각(ms), 진행 중 입장자의 남은 시간 계산용
    private long roundStartedAt;

    // 이번 라운드에 그려진 그림(받은 순서), 진행 중 입장자에게 다시 보여주기용
    private final List<String> drawDataList = Collections.synchronizedList(new ArrayList<>());

    /**
     * 추방 투표(매치와 별개, 방에 하나만)
     **/
    private UUID kickVoteUuid;

    private UUID kickVoteTargetUuid;

    // 투표자 uuid → 찬성 여부
    private final Map<UUID, Boolean> kickVoteBallotMap = new HashMap<>();

    private ScheduledFuture<?> kickVoteTimer;

    // 추방된 사람(방이 메모리에 있는 동안 다시 들어올 수 없음)
    private final Set<UUID> kickedUserUuidSet = new HashSet<>();

    /**
     * 참가자 정보
     **/
    private final Map<UUID, Participant> participantMap = new LinkedHashMap<>();

    private UUID drawerUuid;

    private long lastDrawerJoinSequence;

    private long nextJoinSequence;

    public static GameState of(Long gameroomId, GameroomCreateRequestDto gameroomCreateRequestDto, String encodedPassword,
                               Language language, Level level, UUID hostUuid, List<Long> quizIdList) {

        List<Long> quizIdPool = new ArrayList<>(quizIdList);
        return GameState.builder()
                .gameroomId(gameroomId)
                .maxPlayerSize(gameroomCreateRequestDto.maxPlayerSize())
                .roundSize(gameroomCreateRequestDto.roundSize())
                .roundTime(gameroomCreateRequestDto.roundTime())
                .isLocked(gameroomCreateRequestDto.isLocked())
                .encodedPassword(encodedPassword)
                .learningLanguageId(language.getId())
                .learningLanguageType(language.getType())
                .levelId(level.getId())
                .levelName(level.getLevel())
                .hostUuid(hostUuid)
                .quizIdPool(quizIdPool)
                .quizCursor(quizIdPool.size())   // 끝에 둬서 첫 pick에서 셔플되도록
                .build();
    }

    /**
     * 참가자 관리
     **/
    // 참가자 입장(추가)
    public void addParticipant(UUID userUuid, String name) {participantMap.put(userUuid, Participant.of(userUuid, name, nextJoinSequence++));}

    // 참가자 퇴장(제거)
    public Participant removeParticipant(UUID userUuid) {return participantMap.remove(userUuid);}

    // 참가자 수
    public int getParticipantSize(){return participantMap.size();}

    // 꽉 찬 방 확인
    public boolean isFull() {return participantMap.size() >= maxPlayerSize;}

    // 참가 여부 확인
    public boolean contains(UUID userUuid) {return participantMap.containsKey(userUuid);}

    // 방장 위임
    public UUID changeHost() {
        this.hostUuid = participantMap.keySet().iterator().next();
        return hostUuid;
    }

    // 방장 이름(방 목록 표시용)
    public String getHostName() {return participantMap.get(hostUuid).getName();}

    /**
     * 게임 상태
     **/
    // 게임 시작
    public void startMatch(UUID matchUuid, List<Quiz> quizList) {
        this.waitingNextMatch = false;
        this.playing = true;
        this.matchUuid = matchUuid;
        this.quizList = List.copyOf(quizList);
        this.currentRound = 0;
        this.drawerUuid = null;
        this.lastDrawerJoinSequence = -1;
        this.answered = false;
        drawDataList.clear();
        participantMap.values().forEach(Participant::resetScore);
        countdownEndAt = System.currentTimeMillis() + COUNTDOWN_TIME * 1000L;
        nextMatchStartAt = 0;
    }

    // 게임 종료
    public void finishMatch() {
        cancelRoundTimer();
        playing = false;
        matchUuid = null;
        quizList = List.of();
        currentRound = 0;
        drawerUuid = null;
        answered = false;
        drawDataList.clear();
        countdownEndAt = 0;
    }

    // 자동 시작 조건(플레잉 아님, 다음 매치 웨이팅 상태, 참가자 2명 이상)
    public boolean canStartMatch() {return !playing && !waitingNextMatch && participantMap.size() >= 2;}

    // 매치 끝났을 때 사용
    public void waitNextMatch() {
        this.waitingNextMatch = true;
        this.nextMatchStartAt = System.currentTimeMillis() + NEXT_MATCH_WAIT_TIME * 1000L;
    }

    // 게임 미시작(혼자) 또는 게임 중일 때 사용
    public void notWaitNextMatch() {
        this.waitingNextMatch = false;
        this.nextMatchStartAt = 0;
    }

    public void moveToNextRound() {

        currentRound++;
        answered = false;
        roundStartedAt = System.currentTimeMillis();
        drawDataList.clear();
        quizList.get(currentRound - 1).submitted();

        Participant nextDrawer = participantMap.values().stream()
                .filter(participant -> participant.getJoinSequence() > lastDrawerJoinSequence).findFirst()
                .orElseGet(() -> participantMap.values().iterator().next());

        drawerUuid = nextDrawer.getUserUuid();
        lastDrawerJoinSequence = nextDrawer.getJoinSequence();
    }

    // 다음 라운드 있는지 확인: 실제 문제 수 기준(방 생성 뒤 단어가 삭제돼도 안전)
    public boolean hasNextRound() {return currentRound < quizList.size();}

    // 라운드 진행 중(그림·정답 입력을 받는 중)
    public boolean isRoundInProgress() {return playing && currentRound > 0 && !answered;}

    public boolean isDrawer(UUID userUuid) {return userUuid.equals(drawerUuid);}

    // 이번 라운드 남은 시간(초), 라운드 진행 중이 아니면 0
    public int getRemainingTime() {

        if(!isRoundInProgress())
            return 0;

        long elapsedSeconds = (System.currentTimeMillis() - roundStartedAt) / 1000;
        return (int) Math.max(0, roundTime - elapsedSeconds);
    }

    // 시작 예고 남은 시간(초), 시작 예고 중이 아니면 0
    public int getCountdownRemainingTime() {

        if(!playing || currentRound != 0)
            return 0;

        return remainingSeconds(countdownEndAt);
    }

    // 다음 매치까지 남은 시간(초), 다음 매치 대기 중이 아니면 0
    public int getNextMatchRemainingTime() {

        if(playing || !waitingNextMatch)
            return 0;

        return remainingSeconds(nextMatchStartAt);
    }


    /**
     * 그림
     **/
    public void addDrawData(String drawData) {drawDataList.add(drawData);}

    // 진행 중 입장자용 복사본(라운드 진행 중이 아니면 빈 목록)
    public List<String> getDrawDataSnapshot() {

        if(!isRoundInProgress())
            return List.of();

        synchronized (drawDataList) {
            return List.copyOf(drawDataList);
        }
    }

    /**
     * 추방 투표
     **/
    public boolean isKickVoteInProgress() {return kickVoteTargetUuid != null;}

    // 방장이 시작: 방장은 찬성으로 시작
    public UUID startKickVote(UUID targetUuid, UUID proposerUuid) {

        kickVoteUuid = UUID.randomUUID();
        kickVoteTargetUuid = targetUuid;
        kickVoteBallotMap.clear();
        kickVoteBallotMap.put(proposerUuid, true);
        return kickVoteUuid;
    }

    public void kickVote(UUID voterUuid, boolean agree) {kickVoteBallotMap.put(voterUuid, agree);}

    public boolean hasKickVoted(UUID voterUuid) {return kickVoteBallotMap.containsKey(voterUuid);}

    // 투표 대상자: 지금 방에 있는 사람 중 추방 대상 제외
    public int getKickVoterSize() {return participantMap.size() - (participantMap.containsKey(kickVoteTargetUuid) ? 1 : 0);}

    // 지금 방에 남아 있는 사람의 표만 셈
    public int getKickAgreeSize() {return countKickBallot(true);}

    public int getKickDisagreeSize() {return countKickBallot(false);}

    // 찬성이 투표 대상자의 절반 초과(투표하지 않은 사람은 반대로 침)
    public boolean isKickPassed() {return getKickAgreeSize() * 2 > getKickVoterSize();}

    // 투표 대상자가 모두 투표함(지금 방에 있는 사람 중 대상 제외)
    public boolean isKickAllVoted() {return getKickAgreeSize() + getKickDisagreeSize() >= getKickVoterSize();}

    public void resetKickVoteTimer(ScheduledFuture<?> newTimer) {
        cancelKickVoteTimer();
        this.kickVoteTimer = newTimer;
    }

    // 추방된 사람 기록
    public void addKickedUser(UUID userUuid) {kickedUserUuidSet.add(userUuid);}

    // 추방된 사람인지 확인
    public boolean isKickedUser(UUID userUuid) {return kickedUserUuidSet.contains(userUuid);}

    public void clearKickVote() {
        cancelKickVoteTimer();
        kickVoteUuid = null;
        kickVoteTargetUuid = null;
        kickVoteBallotMap.clear();
    }

    private int countKickBallot(boolean agree) {
        return (int) kickVoteBallotMap.entrySet().stream()
                .filter(ballot -> participantMap.containsKey(ballot.getKey()) && ballot.getValue() == agree)
                .count();
    }

    private void cancelKickVoteTimer() {
        if (kickVoteTimer != null)
            kickVoteTimer.cancel(false);
        kickVoteTimer = null;
    }

    /**
     * 문제
     **/
    // 현재 문제
    public Quiz currentQuiz() {return quizList.get(currentRound - 1);}

    // 정답 처리
    public void answered() {this.answered = true;}

    // 새 라운드 타이머(기존 타이머 종료)
    public void resetRoundTimer(ScheduledFuture<?> newTimer) {
        cancelRoundTimer();
        this.roundTimer = newTimer;
    }

    /**
     * 출제 관리
     **/
    public List<Long> pickQuizIdList() {

        // 라운드 수만큼 풀에서 가져올 것임
        List<Long> pickedQuizIdList = new ArrayList<>(roundSize);

        //라운드 수만큼 선정 퀴즈 리스트 찰 때까지 반복
        while (pickedQuizIdList.size() < roundSize) {

            // 한 바퀴 다 씀: 다시 섞음
            if (quizCursor >= quizIdPool.size()) {
                Collections.shuffle(quizIdPool);
                quizCursor = 0;
            }

            Long quizId = quizIdPool.get(quizCursor++);

            if(!pickedQuizIdList.contains(quizId))
                pickedQuizIdList.add(quizId);
        }

        return pickedQuizIdList;
    }

    // 라운드 종료, 게임 종료
    private void cancelRoundTimer() {
        if (roundTimer != null)
            roundTimer.cancel(false);
        roundTimer = null;
    }

    // 지금부터 endAt까지 남은 시간(초, 올림), 지났으면 0
    private int remainingSeconds(long endAt) {
        return (int) Math.max(0, Math.ceil((endAt - System.currentTimeMillis()) / 1000.0));
    }
}
