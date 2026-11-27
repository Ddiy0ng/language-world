package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.dto.request.GameroomCreateRequestDto;
import com.languageworld.be.domain.game.dto.request.GameroomEnterRequestDto;
import com.languageworld.be.domain.game.dto.response.GameroomCreateResponseDto;
import com.languageworld.be.domain.game.dto.response.GameroomEnterResponseDto;
import com.languageworld.be.domain.game.dto.response.GameroomResponseDto;
import com.languageworld.be.domain.game.dto.response.WebSocketKickVoteEndResponseDto;
import com.languageworld.be.domain.game.dto.response.WebSocketGameroomMemberResponseDto;
import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.entity.Participant;
import com.languageworld.be.domain.game.repository.GameStateManage;
import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.language.service.LanguageUtil;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.domain.vocab.service.VocabMeaningUtil;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.auth.service.PasswordUtil;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameroomService {

    private final GameroomUtil gameroomUtil;
    private final GameStateUtil gameStateUtil;
    private final GameStateManage gameStateManage;
    private final GameMatchService gameMatchService;
    private final UserUtil userUtil;
    private final LanguageUtil languageUtil;
    private final PasswordUtil passwordUtil;
    private final VocabMeaningUtil vocabMeaningUtil;
    private final WebSocketService webSocketService;

    // 생성: 메모리에 GameState 등록(DB 저장 없음) → 방장 자동 입장
    public GameroomCreateResponseDto createGameroom(CustomPrincipal customPrincipal, GameroomCreateRequestDto gameroomCreateRequestDto) {

        // 유효한 사용자 검증
        UUID hostUuid = customPrincipal.uuid();
        userUtil.deletedUserCheckByUserUuid(hostUuid);
        User host = userUtil.getUserByUuid(hostUuid);

        // 실시간 연결이 있어야 함(연결이 끊길 때 자동 퇴장)
        gameroomUtil.webSocketConnectedCheck(hostUuid);

        // 한 사람 한 방
        gameStateUtil.gameroomEnterCheck(hostUuid);

        // 학습 언어와 레벨이 서로 맞는지
        Language language = languageUtil.getLanguageById(gameroomCreateRequestDto.learningLanguageId());
        Level level = languageUtil.getLevelById(gameroomCreateRequestDto.levelId());
        languageUtil.languageLevelMatchCheck(language, level, hostUuid);

        // 잠긴방 비번 암호화
        String encodedPassword = null;
        if(gameroomCreateRequestDto.isLocked()) {
            if(gameroomCreateRequestDto.password() == null || gameroomCreateRequestDto.password().isBlank()) {

                CustomLogger.warn(
                        LogEventCode.GAME,
                        "FAIL",
                        LogEventReasonCode.GAMEROOM_PASSWORD_REQUIRED,
                        LogEventReasonCode.GAMEROOM_PASSWORD_REQUIRED.getMessage(),
                        hostUuid.toString()
                );

                throw new CustomException(CustomExceptionCode.GAMEROOM_PASSWORD_REQUIRED);
            }

            encodedPassword = passwordUtil.encodePassword(gameroomCreateRequestDto.password());
        }

        // 문제 풀(roundSize보다 적으면 생성 불가)
        List<Long> quizIdList = vocabMeaningUtil.getGameQuizIdList(language.getId(), level.getId(), gameroomCreateRequestDto.roundSize(), hostUuid);

        // gameState 생성(방 id는 메모리에서 발급), host를 참가자로 추가, 메모리에 등록
        Long gameroomId = gameStateManage.nextGameroomId();
        GameState gameState = GameState.of(gameroomId, gameroomCreateRequestDto, encodedPassword, language, level, hostUuid, quizIdList);
        gameState.addParticipant(hostUuid, host.getName());
        gameStateUtil.saveGameroomGameState(gameState);

        // 방 목록: 새 방
        gameMatchService.notifyGameroomUpdated(gameState);

        return GameroomCreateResponseDto.from(gameroomId);
    }

    // 목록: 메모리에 있는 모든 방(가득 찬 방 포함, WAIT, STARTED), DB 조회 없음
    public List<GameroomResponseDto> searchGameroomList() {

        return gameStateManage.findGameStateList().stream()
                .map(GameroomResponseDto::from)
                .toList();
    }

    // 입장: 대기 중·진행 중 모두 가능, 2명 이상이면 자동 시작(다음 매치 대기 중이면 대기가 끝날 때 시작)
    public GameroomEnterResponseDto enterGameroom(CustomPrincipal customPrincipal, Long gameroomId, GameroomEnterRequestDto gameroomEnterRequestDto) {

        UUID userUuid = customPrincipal.uuid();
        userUtil.deletedUserCheckByUserUuid(userUuid);
        User user = userUtil.getUserByUuid(userUuid);

        GameState gameState = gameStateUtil.getGameStateById(gameroomId, userUuid);

        // 이미 이 방에 있으면 그냥 성공
        if(gameState.contains(userUuid))
            return GameroomEnterResponseDto.from(gameState);

        // 이 방에서 추방된 사람은 다시 들어올 수 없음
        if(gameState.isKickedUser(userUuid)) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.KICKED_GAMEROOM_USER,
                    LogEventReasonCode.KICKED_GAMEROOM_USER.getMessage()
                            + " - gameroomId: " + gameroomId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.KICKED_GAMEROOM_USER);
        }

        // 실시간 연결이 있어야 함(연결이 끊길 때 자동 퇴장)
        gameroomUtil.webSocketConnectedCheck(userUuid);

        // 정원 초과 시 입장 불가
        if(gameState.isFull()) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.GAMEROOM_FULL,
                    LogEventReasonCode.GAMEROOM_FULL.getMessage()
                            + " - gameroomId: " + gameroomId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.GAMEROOM_FULL);
        }

        // 한 사람 한 방
        gameStateUtil.gameroomEnterCheck(userUuid);

        // 잠긴 방 비밀번호 확인 (잠기지 않은 방은 body 없이 요청 → dto == null)
        if(gameState.isLocked()) {

            String rawPassword = gameroomEnterRequestDto == null ? null : gameroomEnterRequestDto.password();
            if(rawPassword == null || rawPassword.isBlank()) {

                CustomLogger.warn(
                        LogEventCode.GAME,
                        "FAIL",
                        LogEventReasonCode.GAMEROOM_PASSWORD_REQUIRED,
                        LogEventReasonCode.GAMEROOM_PASSWORD_REQUIRED.getMessage()
                                + " - gameroomId: " + gameroomId,
                        userUuid.toString()
                );

                throw new CustomException(CustomExceptionCode.GAMEROOM_PASSWORD_REQUIRED);
            }

            if(!passwordUtil.isPasswordMatches(rawPassword, gameState.getEncodedPassword())) {

                CustomLogger.warn(
                        LogEventCode.GAME,
                        "FAIL",
                        LogEventReasonCode.GAMEROOM_PASSWORD_MISMATCH,
                        LogEventReasonCode.GAMEROOM_PASSWORD_MISMATCH.getMessage()
                                + " - gameroomId: " + gameroomId,
                        userUuid.toString()
                );

                throw new CustomException(CustomExceptionCode.GAMEROOM_PASSWORD_MISMATCH);
            }
        }

        // 입장
        gameState.addParticipant(userUuid, user.getName());

        // 방에 있는 사람들에게 입장 알림
        WebSocketGameroomMemberResponseDto webSocketGameroomMemberResponseDto = WebSocketGameroomMemberResponseDto.from(gameState, userUuid, user.getName());
        webSocketService.sendToRoom(WebSocketPurposeCode.GAME, gameroomId, WebSocketEventResponseDto.from(WebSocketEventTypeCode.JOINED, webSocketGameroomMemberResponseDto));

        // 2명 이상 + 진행 중 아님 + 다음 매치 대기 중 아님 → 시작 예고 → 카운트다운 뒤 1라운드
        if(gameState.canStartMatch())
            gameMatchService.startMatch(gameState, userUuid);

        // 방 목록: 인원 변경
        gameMatchService.notifyGameroomUpdated(gameState);

        // 입장한 본인에게는 응답으로 참가자 목록 + 진행 상태
        return GameroomEnterResponseDto.from(gameState);
    }

    // 퇴장 요청
    public void quitGameroom(CustomPrincipal customPrincipal, Long gameroomId) {

        leaveGameroom(customPrincipal.uuid(), gameroomId);
    }

    // 퇴장 처리(퇴장 요청, STOMP 연결 끊김, 추방 투표에서 공통 사용)
    // 마지막 사람이면 메모리에서 제거, 아니면 방장 위임·퇴장 알림 → 진행 중이면 2명 미만 종료 / 그리는 사람 퇴장 시 라운드 건너뜀
    public void leaveGameroom(UUID userUuid, Long gameroomId) {

        GameState gameState = gameroomUtil.validGameroomMemberCheck(gameroomId, userUuid);

        // 제거 전에 그리는 사람이었는지 확인
        boolean wasDrawer = gameState.isPlaying() && gameState.isDrawer(userUuid);
        Participant leftParticipant = gameState.removeParticipant(userUuid);

        // 마지막 사람: 예약 작업(라운드·다음 매치·추방 투표) 취소 → 메모리에서 제거
        if(gameState.getParticipantSize() == 0) {

            gameState.finishMatch();
            gameState.clearKickVote();
            gameStateUtil.remove(gameroomId);

            // 방 목록: 방 없어짐
            gameMatchService.notifyGameroomRemoved(gameroomId);
            return;
        }

        // 방장이면 가장 먼저 들어온 사람에게 위임(메모리만)
        if(userUuid.equals(gameState.getHostUuid()))
            gameState.changeHost();

        // 남은 사람들에게 퇴장 알림(나간 사람은 목록에서 빠지므로 이름을 같이 보냄, hostUuid로 방장 변경도 전달)
        WebSocketGameroomMemberResponseDto webSocketGameroomMemberResponseDto = WebSocketGameroomMemberResponseDto.from(gameState, userUuid, leftParticipant.getName());
        webSocketService.sendToRoom(WebSocketPurposeCode.GAME, gameroomId, WebSocketEventResponseDto.from(WebSocketEventTypeCode.LEFT, webSocketGameroomMemberResponseDto));

        if(gameState.isPlaying()) {

            // 진행 중 2명 미만: 종료(남은 한 명은 결과를 얻음, 기록 저장은 finishMatch가 따로 트랜잭션)
            if(gameState.getParticipantSize() < 2)
                gameMatchService.finishMatch(gameroomId, userUuid);
            // 그리는 사람이 나감: 이번 라운드 건너뜀
            else if(wasDrawer)
                gameMatchService.skipRound(gameState);
        }

        // 방 목록: 인원·방장 변경
        gameMatchService.notifyGameroomUpdated(gameState);

        // 추방 투표 중: 대상이 나갔거나 투표할 사람이 없으면 취소, 아니면 남은 사람이 모두 투표했는지 다시 확인
        // (추방이 정해지면 대상 퇴장 처리가 이어지므로 맨 마지막에 함)
        if(gameState.isKickVoteInProgress()) {
            if(userUuid.equals(gameState.getKickVoteTargetUuid()) || gameState.getKickVoterSize() == 0)
                endKickVote(gameState, false);
            else
                finishKickVoteIfAllVoted(gameState);
        }
    }

    // 추방 투표 판정: 모두 투표했을 때만 끝냄(결과가 미리 정해져도 기다림), 찬성이 투표 대상자의 절반을 넘으면 추방
    // 모두 투표하지 않으면 제한 시간에 판정(투표하지 않은 사람은 반대)
    public boolean finishKickVoteIfAllVoted(GameState gameState) {

        if(!gameState.isKickAllVoted())
            return false;

        endKickVote(gameState, gameState.isKickPassed());
        return true;
    }

    // 추방 투표 종료: 결과 알림 → 투표 정리 → 추방이면 대상 퇴장 처리(LEFT, 진행 중이면 2명 미만 종료·그리는 사람 건너뜀까지)
    public void endKickVote(GameState gameState, boolean kicked) {

        Long gameroomId = gameState.getGameroomId();
        UUID targetUuid = gameState.getKickVoteTargetUuid();

        webSocketService.sendToRoom(WebSocketPurposeCode.GAME, gameroomId,
                WebSocketEventResponseDto.from(WebSocketEventTypeCode.KICK_VOTE_ENDED, WebSocketKickVoteEndResponseDto.of(gameState, kicked)));
        gameState.clearKickVote();

        if(!kicked)
            return;

        // 추방된 사람은 이 방에 다시 들어올 수 없음(방이 메모리에 있는 동안)
        gameState.addKickedUser(targetUuid);

        if(!gameState.contains(targetUuid))
            return;

        leaveGameroom(targetUuid, gameroomId);

        CustomLogger.info(
                LogEventCode.GAME,
                "SUCCESS",
                SuccessCode.GAME_KICK_SUCCESS.getMessage() + " - gameroomId: " + gameroomId,
                targetUuid.toString()
        );
    }
}
