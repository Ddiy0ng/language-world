package com.languageworld.be.domain.chat.service;

import com.languageworld.be.domain.chat.entity.ChatroomUser;
import com.languageworld.be.domain.chat.repository.ChatroomUserRepository;
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
public class ChatroomUserUtil {

    private final ChatroomUserRepository chatroomUserRepository;

    public ChatroomUser getChatroomUserByChatroomIdAndUserUuid(Long chatroomId, UUID userUuid) {

        Optional<ChatroomUser> optionalChatroomUser = chatroomUserRepository.findByChatroom_IdAndUser_Uuid(chatroomId, userUuid);
        if(optionalChatroomUser.isEmpty()) {

            CustomLogger.warn(LogEventCode.CHAT,
                    "FAIL",
                    LogEventReasonCode. CHATROOM_USER_NOT_FOUND,
                    LogEventReasonCode.CHATROOM_USER_NOT_FOUND.getMessage()
                            + " - UserUuid: " + userUuid,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.CHATROOM_USER_NOT_FOUND);
        }

        return optionalChatroomUser.get();
    }

    public ChatroomUser getOldestChatroomUser(Long chatroomId, UUID userUuid) {

        Optional<ChatroomUser> optionalChatroomUser = chatroomUserRepository.findFirstByChatroom_IdAndUser_UuidNotAndQuitAtIsNullOrderByEnteredAtAsc(chatroomId, userUuid);
        if(optionalChatroomUser.isEmpty()) {

            CustomLogger.warn(LogEventCode.CHAT,
                    "FAIL",
                    LogEventReasonCode. CHATROOM_USER_NOT_FOUND,
                    LogEventReasonCode.CHATROOM_USER_NOT_FOUND.getMessage()
                            + " - UserUuid: " + userUuid,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.CHATROOM_USER_NOT_FOUND);
        }

        return optionalChatroomUser.get();
    }

    public void chatroomUserCheck(Long chatroomId, UUID userUuid) {

        ChatroomUser chatroomUser = getChatroomUserByChatroomIdAndUserUuid(chatroomId, userUuid);


        if(chatroomUser.getQuitAt() != null) {

            CustomLogger.warn(LogEventCode.CHAT,
                    "FAIL",
                    LogEventReasonCode. QUIT_CHATROOM_USER,
                    LogEventReasonCode.QUIT_CHATROOM_USER.getMessage()
                            + " - UserUuid: " + userUuid,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.QUIT_CHATROOM_USER);
        }
    }

}
