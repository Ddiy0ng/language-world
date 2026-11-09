package com.languageworld.be.domain.chat.service;

import com.languageworld.be.domain.chat.entity.Chatroom;
import com.languageworld.be.domain.chat.entity.ChatroomUser;
import com.languageworld.be.domain.chat.repository.ChatroomRepository;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatroomUtil {

    private final ChatroomRepository chatroomRepository;

    public Chatroom getChatroomById(Long chatroomId, UUID userUuid) {

        Optional<Chatroom> optionalChatroom = chatroomRepository.findById(chatroomId);
        if(optionalChatroom.isEmpty()) {
            CustomLogger.warn(LogEventCode.CHAT,
                    "FAIL",
                    LogEventReasonCode. CHATROOM_NOT_FOUND,
                    LogEventReasonCode.CHATROOM_NOT_FOUND.getMessage()
                            + " - chatroomId: " + chatroomId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.CHATROOM_NOT_FOUND);
        }

        return optionalChatroom.get();
    }
}
