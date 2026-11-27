package com.languageworld.be.domain.game.controller;

import com.languageworld.be.domain.game.dto.request.GameChatRequestDto;
import com.languageworld.be.domain.game.dto.request.GameDrawRequestDto;
import com.languageworld.be.domain.game.service.WebSocketGameService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketGameController {

    private final WebSocketGameService webSocketGameService;

    // 채팅·정답: 보내는 건 서비스가 직접(정답이면 CHAT 대신 CORRECT_ANSWER, 그리는 사람이면 본인에게만 알림)
    @MessageMapping("/gameroomId/{gameroomId}/chat")
    public void sendGameChat(@DestinationVariable Long gameroomId,
                             @Valid @Payload GameChatRequestDto gameChatRequestDto,
                             Principal principal) {

        CustomPrincipal customPrincipal = getCustomPrincipal(principal);

        webSocketGameService.sendGameChat(customPrincipal, gameroomId, gameChatRequestDto);

        CustomLogger.info(
                LogEventCode.GAME,
                "SUCCESS",
                SuccessCode.GAME_CHAT_SEND_SUCCESS.getMessage(),
                customPrincipal.uuid().toString()
        );
    }

    // 그림: 자주 오므로 성공 로그는 남기지 않음
    @MessageMapping("/gameroomId/{gameroomId}/draw")
    public void sendDraw(@DestinationVariable Long gameroomId,
                         @Valid @Payload GameDrawRequestDto gameDrawRequestDto,
                         Principal principal) {

        webSocketGameService.sendDraw(getCustomPrincipal(principal), gameroomId, gameDrawRequestDto);
    }

    private CustomPrincipal getCustomPrincipal(Principal principal) {

        if (!(principal instanceof Authentication authentication) || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomPrincipal customPrincipal))
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED);

        return customPrincipal;
    }
}
