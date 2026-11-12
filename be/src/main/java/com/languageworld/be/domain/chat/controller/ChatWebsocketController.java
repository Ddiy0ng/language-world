package com.languageworld.be.domain.chat.controller;

import com.languageworld.be.domain.chat.dto.request.ChatSaveRequestDto;
import com.languageworld.be.domain.chat.service.ChatService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebsocketController {

    private final ChatService chatService;

    @MessageMapping("/chatroomId/{chatroomId}")
    public void sendChat(@DestinationVariable Long chatroomId,
                         @Payload ChatSaveRequestDto chatSaveRequestDto,
                         CustomPrincipal customPrincipal) {

        chatService.saveChat(customPrincipal, chatroomId, chatSaveRequestDto);
        CustomLogger.info(
                LogEventCode.CHAT,
                "SUCCESS",
                SuccessCode.CHAT_SEND_SUCCESS.getMessage(),
                customPrincipal.uuid().toString()
        );
    }
}
