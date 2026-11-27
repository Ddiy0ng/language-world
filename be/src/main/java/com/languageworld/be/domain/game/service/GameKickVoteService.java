package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.dto.request.GameKickVoteRequestDto;
import com.languageworld.be.domain.game.dto.request.GameKickVoteStartRequestDto;
import com.languageworld.be.domain.game.dto.response.WebSocketKickVoteStartResponseDto;
import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.repository.GameStateManage;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
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

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// 방장이 추방 투표를 시작하고, 대상을 뺀 참가자의 절반 초과가 찬성하면 추방(투표하지 않으면 반대)
@Service
@RequiredArgsConstructor
public class GameKickVoteService {

    private final GameroomUtil gameroomUtil;
    private final GameroomService gameroomService;
    private final GameStateManage gameStateManage;
    private final WebSocketService webSocketService;
    private final ScheduledExecutorService gameScheduler;

    // 방장: 추방 투표 시작(방장은 찬성으로 시작) → 모두 투표했으면 바로 끝, 아니면 제한 시간 예약
    public void startKickVote(CustomPrincipal customPrincipal, Long gameroomId, GameKickVoteStartRequestDto gameKickVoteStartRequestDto) {

        UUID hostUuid = customPrincipal.uuid();
        GameState gameState = gameroomUtil.validGameroomMemberCheck(gameroomId, hostUuid);

        if(!hostUuid.equals(gameState.getHostUuid())) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.NOT_GAMEROOM_HOST,
                    LogEventReasonCode.NOT_GAMEROOM_HOST.getMessage()
                            + " - gameroomId: " + gameroomId,
                    hostUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.NOT_GAMEROOM_HOST);
        }

        if(gameState.isKickVoteInProgress()) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.KICK_VOTE_ALREADY_IN_PROGRESS,
                    LogEventReasonCode.KICK_VOTE_ALREADY_IN_PROGRESS.getMessage()
                            + " - gameroomId: " + gameroomId,
                    hostUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.KICK_VOTE_ALREADY_IN_PROGRESS);
        }

        // 자기 자신이나 방에 없는 사람은 대상이 될 수 없음
        UUID targetUuid = gameKickVoteStartRequestDto.targetUuid();
        if(targetUuid.equals(hostUuid) || !gameState.contains(targetUuid)) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.INVALID_KICK_TARGET,
                    LogEventReasonCode.INVALID_KICK_TARGET.getMessage()
                            + " - gameroomId: " + gameroomId,
                    hostUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.INVALID_KICK_TARGET);
        }

        UUID kickVoteUuid = gameState.startKickVote(targetUuid, hostUuid);
        String targetName = gameState.getParticipantMap().get(targetUuid).getName();

        sendToGameroom(gameroomId, WebSocketEventTypeCode.KICK_VOTE_STARTED, WebSocketKickVoteStartResponseDto.of(gameState, targetName));

        CustomLogger.info(
                LogEventCode.GAME,
                "SUCCESS",
                SuccessCode.KICK_VOTE_START_SUCCESS.getMessage() + " - gameroomId: " + gameroomId + ", targetUuid: " + targetUuid,
                hostUuid.toString()
        );

        // 대상 말고 방장 혼자면 방장 표로 바로 결정됨
        if(gameroomService.finishKickVoteIfAllVoted(gameState))
            return;

        // 제한 시간: 그때까지 모두 투표하지 않으면 그때의 찬성 수로 판정(투표하지 않은 사람은 반대)
        gameState.resetKickVoteTimer(gameScheduler.schedule(() -> {
            try {
                timeoutKickVote(gameroomId, kickVoteUuid);
            } catch (Exception e) {
                CustomLogger.error(
                        LogEventCode.GAME,
                        "FAIL",
                        LogEventReasonCode.GAME_SCHEDULED_TASK_FAILED,
                        LogEventReasonCode.GAME_SCHEDULED_TASK_FAILED.getMessage() + " - (kick vote) gameroomId: " + gameroomId,
                        e
                );
            }
        }, GameState.KICK_VOTE_TIME, TimeUnit.SECONDS));
    }

    // 참가자(대상 제외): 찬반 투표 → 모두 투표하면 끝
    public void kickVote(CustomPrincipal customPrincipal, Long gameroomId, GameKickVoteRequestDto gameKickVoteRequestDto) {

        UUID voterUuid = customPrincipal.uuid();
        GameState gameState = gameroomUtil.validGameroomMemberCheck(gameroomId, voterUuid);

        if(!gameState.isKickVoteInProgress()) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.KICK_VOTE_NOT_IN_PROGRESS,
                    LogEventReasonCode.KICK_VOTE_NOT_IN_PROGRESS.getMessage()
                            + " - gameroomId: " + gameroomId,
                    voterUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.KICK_VOTE_NOT_IN_PROGRESS);
        }

        if(voterUuid.equals(gameState.getKickVoteTargetUuid())) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.KICK_TARGET_CANNOT_VOTE,
                    LogEventReasonCode.KICK_TARGET_CANNOT_VOTE.getMessage()
                            + " - gameroomId: " + gameroomId,
                    voterUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.KICK_TARGET_CANNOT_VOTE);
        }

        if(gameState.hasKickVoted(voterUuid)) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.ALREADY_KICK_VOTED,
                    LogEventReasonCode.ALREADY_KICK_VOTED.getMessage()
                            + " - gameroomId: " + gameroomId,
                    voterUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.ALREADY_KICK_VOTED);
        }

        gameState.kickVote(voterUuid, gameKickVoteRequestDto.agree());

        CustomLogger.info(
                LogEventCode.GAME,
                "SUCCESS",
                SuccessCode.KICK_VOTE_SUCCESS.getMessage() + " - gameroomId: " + gameroomId,
                voterUuid.toString()
        );

        gameroomService.finishKickVoteIfAllVoted(gameState);
    }

    // 제한 시간 초과(예약 스레드): 같은 투표가 아직 진행 중일 때만
    private void timeoutKickVote(Long gameroomId, UUID kickVoteUuid) {

        gameStateManage.findOptionalGameState(gameroomId)
                .filter(gameState -> kickVoteUuid.equals(gameState.getKickVoteUuid()))
                .ifPresent(gameState -> gameroomService.endKickVote(gameState, gameState.isKickPassed()));
    }

    private void sendToGameroom(Long gameroomId, WebSocketEventTypeCode webSocketEventTypeCode, Object data) {
        webSocketService.sendToRoom(WebSocketPurposeCode.GAME, gameroomId, WebSocketEventResponseDto.from(webSocketEventTypeCode, data));
    }
}
