package com.languageworld.be.domain.game.controller;

import com.languageworld.be.domain.game.dto.request.GameChatRequestDto;
import com.languageworld.be.domain.game.dto.response.GameChatResponseDto;
import com.languageworld.be.domain.game.service.WebSocketGameService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import com.languageworld.be.global.webSocket.dto.WebSocketEventResponseDto;
import com.languageworld.be.global.webSocket.enumGroup.WebSocketEventTypeCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketGameController {

    private final WebSocketGameService webSocketGameService;

    @MessageMapping("/gameroomId/{gameroomId}/chat")
    @SendTo("/sub/gameroomId/{gameroomId}")
    public WebSocketEventResponseDto<GameChatResponseDto> sendGameChat(@DestinationVariable Long gameroomId,
                                                                       @Valid @Payload GameChatRequestDto gameChatRequestDto,
                                                                       Principal principal) {

        if (!(principal instanceof Authentication authentication) || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomPrincipal customPrincipal))
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED);

        GameChatResponseDto gameChatResponseDto = webSocketGameService.sendGameChat(customPrincipal, gameroomId, gameChatRequestDto);

        CustomLogger.info(
                LogEventCode.GAME,
                "SUCCESS",
                SuccessCode.GAME_CHAT_SEND_SUCCESS.getMessage(),
                customPrincipal.uuid().toString()
        );

        return WebSocketEventResponseDto.from(WebSocketEventTypeCode.CHAT, gameChatResponseDto);
    }
}
