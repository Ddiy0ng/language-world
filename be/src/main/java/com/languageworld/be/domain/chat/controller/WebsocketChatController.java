package com.languageworld.be.domain.chat.controller;

import com.languageworld.be.domain.chat.dto.request.ChatSaveRequestDto;
import com.languageworld.be.domain.chat.dto.response.ChatResponseDto;
import com.languageworld.be.domain.chat.service.WebSocketChatService;
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
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebsocketChatController {

    private final WebSocketChatService webSocketChatService;

    @MessageMapping("/chatroomId/{chatroomId}")
    @SendTo("/sub/chatroomId/{chatroomId}")
    public ChatResponseDto sendChat(@DestinationVariable Long chatroomId,
                         @Valid @Payload ChatSaveRequestDto chatSaveRequestDto,
                         Principal principal) {

        if (!(principal instanceof Authentication authentication) || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomPrincipal customPrincipal))
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED);

        ChatResponseDto chatResponseDto = webSocketChatService.sendChat(customPrincipal, chatroomId, chatSaveRequestDto);

        CustomLogger.info(
                LogEventCode.CHAT,
                "SUCCESS",
                SuccessCode.CHAT_SEND_SUCCESS.getMessage(),
                customPrincipal.uuid().toString()
        );

        return chatResponseDto;
    }
}
