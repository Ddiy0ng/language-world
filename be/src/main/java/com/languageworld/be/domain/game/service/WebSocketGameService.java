package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.dto.request.GameChatRequestDto;
import com.languageworld.be.domain.game.dto.request.GameDrawRequestDto;
import com.languageworld.be.domain.game.dto.response.WebSocketGameChatResponseDto;
import com.languageworld.be.domain.game.dto.response.WebSocketGameDrawResponseDto;
import com.languageworld.be.domain.game.dto.response.WebSocketPersonalNoticeResponseDto;
import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.entity.Participant;
import com.languageworld.be.domain.game.entity.Quiz;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.webSocket.dto.WebSocketEventResponseDto;
import com.languageworld.be.global.webSocket.enumGroup.WebSocketEventTypeCode;
import com.languageworld.be.global.webSocket.enumGroup.WebSocketPurposeCode;
import com.languageworld.be.global.webSocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebSocketGameService {

    private final GameroomUtil gameroomUtil;
    private final GameMatchService gameMatchService;
    private final WebSocketService webSocketService;

    // 채팅: 라운드 진행 중이고 정답이면 정답 처리(CHAT으로 보내지 않음), 아니면 방 전체에 CHAT
    public void sendGameChat(CustomPrincipal customPrincipal, Long gameroomId, GameChatRequestDto gameChatRequestDto) {

        UUID userUuid = customPrincipal.uuid();

        // 메모리에 방이 있고 참가자인지
        GameState gameState = gameroomUtil.validGameroomMemberCheck(gameroomId, userUuid);
        Participant sender = gameState.getParticipantMap().get(userUuid);
        String chatMessage = gameChatRequestDto.chatMessage();

        if(gameState.isRoundInProgress()) {

            Quiz currentQuiz = gameState.currentQuiz();
            if(currentQuiz.matches(chatMessage)) {

                // 그리는 사람이 정답을 치면: 본인에게만 알림, 방에는 아무것도 안 보냄
                if(gameState.isDrawer(userUuid)) {

                    CustomLogger.warn(
                            LogEventCode.GAME,
                            "FAIL",
                            LogEventReasonCode.DRAWER_CANNOT_SEND_ANSWER,
                            LogEventReasonCode.DRAWER_CANNOT_SEND_ANSWER.getMessage()
                                    + " - gameroomId: " + gameroomId,
                            userUuid.toString()
                    );

                    webSocketService.sendToGameUser(
                            gameroomId,
                            userUuid,
                            WebSocketEventResponseDto.from(
                                    WebSocketEventTypeCode.ANSWER_NOT_ALLOWED,
                                    WebSocketPersonalNoticeResponseDto.from(LogEventReasonCode.DRAWER_CANNOT_SEND_ANSWER.getMessage())));
                    return;
                }

                gameMatchService.correctAnswer(gameState, sender, currentQuiz);
                return;
            }
        }

        webSocketService.sendToRoom(
                WebSocketPurposeCode.GAME,
                gameroomId,
                WebSocketEventResponseDto.from(WebSocketEventTypeCode.CHAT, WebSocketGameChatResponseDto.of(sender, chatMessage)));
    }

    // 그림: 라운드 진행 중인 그리는 사람만
    public void sendDraw(CustomPrincipal customPrincipal, Long gameroomId, GameDrawRequestDto gameDrawRequestDto) {

        UUID userUuid = customPrincipal.uuid();
        GameState gameState = gameroomUtil.validGameroomMemberCheck(gameroomId, userUuid);

        if(!gameState.isRoundInProgress() || !gameState.isDrawer(userUuid)) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.NOT_GAME_DRAWER,
                    LogEventReasonCode.NOT_GAME_DRAWER.getMessage()
                            + " - gameroomId: " + gameroomId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.NOT_GAME_DRAWER);
        }

        // 진행 중 입장자에게 다시 보여주려고 이번 라운드 그림을 받은 순서대로 기록
        gameState.addDrawData(gameDrawRequestDto.drawData());

        webSocketService.sendToRoom(
                WebSocketPurposeCode.GAME,
                gameroomId,
                WebSocketEventResponseDto.from(WebSocketEventTypeCode.DRAW_TURN, WebSocketGameDrawResponseDto.from(gameDrawRequestDto.drawData())));
    }
}
