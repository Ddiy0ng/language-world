package com.languageworld.be.domain.chat.service;

import com.languageworld.be.domain.chat.dto.request.ChatSaveRequestDto;
import com.languageworld.be.domain.chat.dto.response.ChatResponseDto;
import com.languageworld.be.domain.chat.entity.Chat;
import com.languageworld.be.domain.chat.entity.Chatroom;
import com.languageworld.be.domain.chat.repository.ChatRepository;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebSocketChatService {

    private final ChatroomUserUtil chatroomUserUtil;
    private final UserUtil userUtil;
    private final ChatroomUtil chatroomUtil;
    private final ChatRepository chatRepository;

    @Transactional
    public ChatResponseDto sendChat(CustomPrincipal customPrincipal, Long chatroomId, ChatSaveRequestDto chatSaveRequestDto) {

        UUID userUuid = customPrincipal.uuid();

        chatroomUserUtil.ValidChatroomUserCheck(chatroomId, userUuid);

        User sender = userUtil.getUserByUuid(userUuid);

        Chatroom chatroom = chatroomUtil.getChatroomById(chatroomId, userUuid);

        Chat chat = Chat.of(chatSaveRequestDto.chatMessage(),sender, chatroom);

        chatRepository.save(chat);
        CustomLogger.info(
                LogEventCode.CHAT,
                "SUCCESS",
                SuccessCode.CHAT_SAVE_SUCCESS.getMessage(),
                customPrincipal.uuid().toString()
        );

        return ChatResponseDto.from(chat, UserSummaryResponseDto.from(sender), chatroomId);
    }
}
