package com.languageworld.be.domain.chat.service;

import com.languageworld.be.domain.chat.entity.Chatroom;
import com.languageworld.be.domain.chat.entity.ChatroomUser;
import com.languageworld.be.domain.chat.enumGroup.ChatroomTypeCode;
import com.languageworld.be.domain.chat.repository.ChatroomUserRepository;
import com.languageworld.be.domain.user.service.BlockedUserUtil;
import com.languageworld.be.domain.user.service.UserUtil;
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
    private final UserUtil userUtil;
    private final ChatroomUtil chatroomUtil;
    private final BlockedUserUtil blockedUserUtil;

    //채팅방 멤버 + 회원 탈퇴x + 퇴장 상태x + PERSONAL일 경우 차단 X
    public void getValidChatroomUser(Long chatroomId, UUID userUuid) {

        ChatroomUser chatroomUser = getChatroomUserByChatroomIdAndUserUuid(chatroomId, userUuid);

        // 회원 탈퇴 x
        userUtil.deletedUserCheckByUserUuid(userUuid);

        // 퇴장 상태 x
        chatroomUserQuitCheck(chatroomUser);

        // Personal일 경우 차단 x인지
        Chatroom chatroom = chatroomUtil.getChatroomById(chatroomId, userUuid);
        if(chatroom.getChatroomType() == ChatroomTypeCode.PERSONAL) {

            Optional<ChatroomUser> optionalOpposite = chatroomUserRepository.findByChatroom_IdAndUser_UuidNot(chatroomId, userUuid);
            if(optionalOpposite.isEmpty()) {
                CustomLogger.warn(LogEventCode.CHAT,
                        "FAIL",
                        LogEventReasonCode.CHATROOM_USER_NOT_FOUND,
                        LogEventReasonCode.CHATROOM_USER_NOT_FOUND.getMessage()
                                + " - UserUuid: " + userUuid,
                        userUuid.toString()
                );

                throw new CustomException(CustomExceptionCode.CHATROOM_USER_NOT_FOUND);
            }

            ChatroomUser opposite = optionalOpposite.get();
            blockedUserUtil.blockedUserCheck(userUuid, opposite.getUser().getUuid());
        }
    }

    // chatroom에 해당 uuid의 멤버가 있는가
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

    // 퇴장한 히스토리 유저인지 확인
    public void chatroomUserQuitCheck(ChatroomUser chatroomUser) {

        if(chatroomUser.getQuitAt() != null) {

            CustomLogger.warn(LogEventCode.CHAT,
                    "FAIL",
                    LogEventReasonCode. QUIT_CHATROOM_USER,
                    LogEventReasonCode.QUIT_CHATROOM_USER.getMessage()
                            + " - UserUuid: " + chatroomUser.getUser().getUuid(),
                    null
            );

            throw new CustomException(CustomExceptionCode.QUIT_CHATROOM_USER);
        }
    }

    // 기존 멤버 중 가장 오래된 멤버를 방장으로 하기 위해
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
}
