package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.dto.response.*;
import com.languageworld.be.domain.game.entity.GameResult;
import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.entity.Participant;
import com.languageworld.be.domain.game.entity.Quiz;
import com.languageworld.be.domain.game.repository.GameResultRepository;
import com.languageworld.be.domain.game.repository.GameStateManage;
import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.language.service.LanguageUtil;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.domain.vocab.entity.VocabMeaning;
import com.languageworld.be.domain.vocab.service.VocabMeaningUtil;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import com.languageworld.be.global.webSocket.dto.WebSocketEventResponseDto;
import com.languageworld.be.global.webSocket.enumGroup.WebSocketEventTypeCode;
import com.languageworld.be.global.webSocket.enumGroup.WebSocketPurposeCode;
import com.languageworld.be.global.webSocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GameMatchService {

    private final GameStateManage gameStateManage;
    private final GameResultRepository gameResultRepository;
    private final GameroomQuizUtil gameroomQuizUtil;
    private final GameStateUtil gameStateUtil;
    private final VocabMeaningUtil vocabMeaningUtil;
    private final LanguageUtil languageUtil;
    private final UserUtil userUtil;
    private final WebSocketService webSocketService;
    private final ScheduledExecutorService gameScheduler;
    private final TransactionTemplate transactionTemplate;

    /**
     * 시작
     **/
    // 문제 뽑기 → 매치 시작 → 시작 예고(카운트다운) → 카운트다운 뒤 1라운드 예약 (재시작이면 userUuid == null)
    public void startMatch(GameState gameState, UUID userUuid) {

        Long gameroomId = gameState.getGameroomId();
        UUID matchUuid = UUID.randomUUID();

        List<VocabMeaning> vocabMeaningList = vocabMeaningUtil.getVocabMeaningListByPickedQuizIdList(gameState.pickQuizIdList());

        List<Quiz> quizList = new ArrayList<>(vocabMeaningList.size());
        for (VocabMeaning vocabMeaning : vocabMeaningList) {

            quizList.add(Quiz.of(vocabMeaning.getId(), vocabMeaning.getVocab().getVocab()));
        }

        gameState.startMatch(matchUuid, quizList);

        // 프론트: "3초 후 게임을 시작합니다" + 카운트다운
        sendToGameroom(gameroomId, WebSocketEventTypeCode.GAME_WILL_BE_STARTED, WebSocketGameWillBeStartedResponseDto.from());

        // 서버: 카운트다운 뒤 1라운드
        schedule(gameState, () -> startNextRound(gameroomId, matchUuid), GameState.COUNTDOWN_TIME);

        // 방 목록: 진행 중으로 바뀜
        notifyGameroomUpdated(gameState);

        CustomLogger.info(
                LogEventCode.GAME,
                "SUCCESS",
                SuccessCode.GAME_START_SUCCESS.getMessage() + " - gameroomId: " + gameroomId + ", matchUuid: " + matchUuid,
                userUuid == null ? null : userUuid.toString()
        );
    }

    /**
     * 라운드 진행
     **/
    // 다음 라운드 시작(예약 스레드): 남은 라운드가 없으면 종료, 있으면 그리는 사람 지정 → 알림 → 제한 시간 예약
    private void startNextRound(Long gameroomId, UUID matchUuid) {

        // 예약 사이에 종료·재시작·삭제됐으면 아무것도 안 함
        GameState gameState = gameStateUtil.getPlayingGameState(gameroomId, matchUuid);
        if(gameState == null)
            return;

        if(!gameState.hasNextRound()) {
            finishMatch(gameroomId, null);
            return;
        }

        // 라운드 번호 증가, 이번 문제 출제 표시, 그리는 사람 지정, 시작 시각 기록
        gameState.moveToNextRound();
        sendToGameroom(gameroomId, WebSocketEventTypeCode.ROUND_STARTED, WebSocketRoundStartResponseDto.from(gameState));

        // 그리는 사람에게만 이번 라운드 단어(개인 경로)
        webSocketService.sendToGameUser(
                gameroomId,
                gameState.getDrawerUuid(),
                WebSocketEventResponseDto.from(
                        WebSocketEventTypeCode.DRAWER_QUIZ,
                        GameQuizResponseDto.of(gameState.getCurrentRound(), gameState.currentQuiz().getAnswer())));

        int round = gameState.getCurrentRound();
        schedule(gameState, () -> timeoutRound(gameroomId, matchUuid, round), gameState.getRoundTime());
    }

    // 제한 시간 초과(예약 스레드): 아직 그 라운드가 진행 중일 때만 정답 공개 후 다음 라운드 예약
    private void timeoutRound(Long gameroomId, UUID matchUuid, int round) {

        GameState gameState = gameStateUtil.getPlayingGameState(gameroomId, matchUuid);
        if(gameState == null || gameState.getCurrentRound() != round || !gameState.isRoundInProgress())
            return;

        gameState.answered();
        sendToGameroom(gameroomId, WebSocketEventTypeCode.ROUND_TIMEOUT, WebSocketRoundEndResponseDto.from(gameState));
        scheduleNextRound(gameState);
    }

    // 정답(채팅 스레드): 라운드 종료 → 점수 → 맞힌 사람·정답 알림 → 다음 라운드 예약(제한 시간 예약은 취소됨)
    public void correctAnswer(GameState gameState, Participant answerer, Quiz quiz) {

        gameState.answered();
        answerer.addScore(quiz.getScore());

        sendToGameroom(gameState.getGameroomId(), WebSocketEventTypeCode.CORRECT_ANSWER, WebSocketCorrectResponseDto.of(gameState, answerer));
        scheduleNextRound(gameState);
    }

    // 그리는 사람 퇴장: 진행 중인 라운드를 건너뜀
    public void skipRound(GameState gameState) {

        if(!gameState.isRoundInProgress())
            return;

        gameState.answered();
        sendToGameroom(gameState.getGameroomId(), WebSocketEventTypeCode.ROUND_SKIPPED, WebSocketRoundEndResponseDto.from(gameState));
        scheduleNextRound(gameState);
    }

    /**
     * 종료
     **/
    // 출제 기록·결과 저장 → 결과 알림 → 메모리 정리 → 2명 이상이면 10초 뒤 다음 매치
    // 저장이 실패해도 매치는 끝나야 하므로(안 끝나면 방이 계속 게임 중으로 남음) 저장만 따로 트랜잭션·예외 처리
    public void finishMatch(Long gameroomId, UUID userUuid) {

        Optional<GameState> optionalGameState = gameStateManage.findOptionalGameState(gameroomId);
        if(optionalGameState.isEmpty() || !optionalGameState.get().isPlaying())
            return;

        GameState gameState = optionalGameState.get();
        UUID matchUuid = gameState.getMatchUuid();

        // 순위는 한 번만 계산해 저장과 알림에 같이 씀
        List<WebSocketGameResultResponseDto> webSocketGameResultResponseDtoList = WebSocketGameResultResponseDto.listFrom(gameState);

        // DB: 출제된 문제, 끝까지 남은 사람의 결과
        saveMatchRecord(gameState, webSocketGameResultResponseDtoList);

        // 결과 창 데이터: 매치 정보가 초기화되기 전에 만듦
        WebSocketMatchEndResponseDto webSocketMatchEndResponseDto = WebSocketMatchEndResponseDto.from(gameState, webSocketGameResultResponseDtoList);

        // 메모리: 예약 작업 취소, 매치 정보 초기화
        gameState.finishMatch();

        sendToGameroom(gameroomId, WebSocketEventTypeCode.GAME_ENDED, webSocketMatchEndResponseDto);

        // 방 목록: 대기 중으로 바뀜
        notifyGameroomUpdated(gameState);

        // 2명 이상 남았으면 10초 뒤 다음 매치(평소처럼 시작 예고·카운트다운부터)
        if(gameState.getParticipantSize() >= 2)
            scheduleNextMatch(gameState);

        CustomLogger.info(
                LogEventCode.GAME,
                "SUCCESS",
                SuccessCode.GAME_END_SUCCESS.getMessage() + " - gameroomId: " + gameroomId + ", matchUuid: " + matchUuid,
                userUuid == null ? null : userUuid.toString()
        );
    }

    // 매치 기록 저장(새 트랜잭션): 실패하면 로그만 남기고 매치 종료는 계속 진행
    private void saveMatchRecord(GameState gameState, List<WebSocketGameResultResponseDto> webSocketGameResultResponseDtoList) {

        try {
            transactionTemplate.executeWithoutResult(status -> {
                gameroomQuizUtil.saveGameroomQuiz(gameState);
                saveGameResult(gameState, webSocketGameResultResponseDtoList);
            });
        } catch (Exception e) {
            CustomLogger.error(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.GAME_RESULT_SAVE_FAILED,
                    LogEventReasonCode.GAME_RESULT_SAVE_FAILED.getMessage()
                            + " - gameroomId: " + gameState.getGameroomId() + ", matchUuid: " + gameState.getMatchUuid(),
                    e
            );
        }
    }

    // 결과 저장: 순위는 결과 목록 그대로, 언어·레벨은 조회 없이 참조로, 사용자는 한 번에 조회
    private void saveGameResult(GameState gameState, List<WebSocketGameResultResponseDto> webSocketGameResultResponseDtoList) {

        Language learningLanguage = languageUtil.getLanguageReference(gameState.getLearningLanguageId());
        Level level = languageUtil.getLevelReference(gameState.getLevelId());

        List<User> userList = userUtil.getUserListByUuidList(webSocketGameResultResponseDtoList.stream()
                .map(WebSocketGameResultResponseDto::userUuid)
                .toList());

        List<GameResult> gameResultList = new ArrayList<>(webSocketGameResultResponseDtoList.size());
        for(WebSocketGameResultResponseDto webSocketGameResultResponseDto : webSocketGameResultResponseDtoList) {

            User user = userList.stream()
                    .filter(foundUser -> foundUser.getUuid().equals(webSocketGameResultResponseDto.userUuid()))
                    .findFirst()
                    .orElse(null);

            // 조회되지 않는 사용자는 결과를 남기지 않음
            if(user == null)
                continue;

            Participant participant = gameState.getParticipantMap().get(webSocketGameResultResponseDto.userUuid());
            gameResultList.add(GameResult.of(
                    gameState.getMatchUuid(),
                    webSocketGameResultResponseDto.rank(),
                    webSocketGameResultResponseDto.score(),
                    participant.getAnswerSize(),
                    learningLanguage,
                    level,
                    user));
        }

        gameResultRepository.saveAll(gameResultList);
    }

    /**
     * 다음 매치
     **/
    // 다음 매치 대기: 이 동안 입장해도 바로 시작하지 않음(canStartMatch == false)
    private void scheduleNextMatch(GameState gameState) {

        Long gameroomId = gameState.getGameroomId();
        gameState.waitNextMatch();
        schedule(gameState, () -> restartMatch(gameroomId), GameState.NEXT_MATCH_WAIT_TIME);
    }

    // 다음 매치 시작(예약 스레드): 그 사이 2명 미만이 됐으면 대기만 풀고, 다음 입장 때 시작
    private void restartMatch(Long gameroomId) {

        Optional<GameState> optionalGameState = gameStateManage.findOptionalGameState(gameroomId);
        if(optionalGameState.isEmpty())
            return;

        GameState gameState = optionalGameState.get();
        if(gameState.isPlaying() || !gameState.isWaitingNextMatch())
            return;

        gameState.notWaitNextMatch();
        if(gameState.getParticipantSize() < 2)
            return;

        startMatch(gameState, null);
    }

    /**
     * 방 목록 실시간 갱신(/sub/gameroomList)
     **/
    // 방 정보가 바뀜(생성, 입장·퇴장으로 인원 변경, 시작·종료로 상태 변경)
    public void notifyGameroomUpdated(GameState gameState) {
        webSocketService.sendToGameroomList(WebSocketEventResponseDto.from(WebSocketEventTypeCode.GAMEROOM_UPDATED, GameroomResponseDto.from(gameState)));
    }

    // 방이 없어짐(마지막 사람 퇴장), data는 gameroomId
    public void notifyGameroomRemoved(Long gameroomId) {
        webSocketService.sendToGameroomList(WebSocketEventResponseDto.from(WebSocketEventTypeCode.GAMEROOM_REMOVED, gameroomId));
    }

    /**
     * 내부 도구
     **/
    // 라운드 사이 쉬는 시간 뒤 다음 라운드
    private void scheduleNextRound(GameState gameState) {

        Long gameroomId = gameState.getGameroomId();
        UUID matchUuid = gameState.getMatchUuid();
        schedule(gameState, () -> startNextRound(gameroomId, matchUuid), GameState.ROUND_INTERVAL_TIME);
    }

    // 예약: roundTimer에 넣어 두면 다음 예약·finishMatch가 취소함, 예약 스레드의 예외는 로그로 남김(그냥 두면 사라짐)
    private void schedule(GameState gameState, Runnable task, int delaySeconds) {

        gameState.resetRoundTimer(gameScheduler.schedule(() -> {
            try {
                task.run();
            } catch (Exception e) {
                CustomLogger.error(
                        LogEventCode.GAME,
                        "FAIL",
                        LogEventReasonCode.GAME_SCHEDULED_TASK_FAILED,
                        LogEventReasonCode.GAME_SCHEDULED_TASK_FAILED.getMessage() + " - gameroomId: " + gameState.getGameroomId(),
                        e
                );
            }
        }, delaySeconds, TimeUnit.SECONDS));
    }

    private void sendToGameroom(Long gameroomId, WebSocketEventTypeCode webSocketEventTypeCode, Object data) {
        webSocketService.sendToRoom(WebSocketPurposeCode.GAME, gameroomId, WebSocketEventResponseDto.from(webSocketEventTypeCode, data));
    }
}
