package com.languageworld.be.domain.user.service;

import com.languageworld.be.domain.user.entity.Friend;
import com.languageworld.be.domain.user.enumGroup.FriendStatusCode;
import com.languageworld.be.domain.user.repository.FriendRepository;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FriendUtil {

    private final FriendRepository friendRepository;

    protected Friend getReceivedFriendRequest(UUID authenticatedUserUuid,Long requestId) {

        Optional<Friend> optionalFriend = friendRepository.findByIdAndFriend_Uuid(requestId, authenticatedUserUuid);

        if(optionalFriend.isEmpty()) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.RECEIVED_FRIEND_REQUEST_NOT_FOUND,
                    LogEventReasonCode.RECEIVED_FRIEND_REQUEST_NOT_FOUND.getMessage()
                            + " - userUuid: " + authenticatedUserUuid
                            + " - requestId: " + requestId,
                    authenticatedUserUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.RECEIVED_FRIEND_REQUEST_NOT_FOUND);
        }

        return optionalFriend.get();
    }

    protected Friend getSentFriendRequest(UUID authenticatedUserUuid,Long requestId) {

        Optional<Friend> optionalFriend = friendRepository.findByIdAndUser_Uuid(requestId, authenticatedUserUuid);

        if(optionalFriend.isEmpty()) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.SENT_FRIEND_REQUEST_NOT_FOUND,
                    LogEventReasonCode.SENT_FRIEND_REQUEST_NOT_FOUND.getMessage()
                            + " - userUuid: " + authenticatedUserUuid
                            + " - requestId: " + requestId,
                    authenticatedUserUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.SENT_FRIEND_REQUEST_NOT_FOUND);
        }

        return optionalFriend.get();
    }

    // friend 또는 user가 authenticatedUser인 것을 찾기
    protected Friend getAcceptedFriendRequest(UUID authenticatedUserUuid, UUID friendUuid) {

        Optional<Friend> optionalFriend = friendRepository.findByUserPariAndStatus(authenticatedUserUuid, friendUuid, FriendStatusCode.ACCEPTED);

        if(optionalFriend.isEmpty()) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.ACCEPTED_FRIEND_REQUEST_NOT_FOUND,
                    LogEventReasonCode.ACCEPTED_FRIEND_REQUEST_NOT_FOUND.getMessage()
                            + " - userUuid: " + authenticatedUserUuid
                            + " - friendUuid: " + friendUuid,
                    authenticatedUserUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.ACCEPTED_FRIEND_REQUEST_NOT_FOUND);
        }

        return optionalFriend.get();
    }

    protected void validateFriendStatus(Friend friend, FriendStatusCode requiredFriendStatusCode, UUID authenticatedUserUuid) {

        if(friend.getFriendStatus() != requiredFriendStatusCode) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.NOT_PROPER_STATUS_FOR_THE_REQUEST,
                    LogEventReasonCode.NOT_PROPER_STATUS_FOR_THE_REQUEST.getMessage()
                            + " - requiredStatus: " + requiredFriendStatusCode
                            + " - actualStatus: " + friend.getFriendStatus(),
                    authenticatedUserUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.NOT_PROPER_STATUS_FOR_THE_REQUEST);
        }
    }

    protected void validateFriendStatusForCancel(Friend friend, UUID authenticatedUserUuid) {

        if(friend.getFriendStatus() == FriendStatusCode.ACCEPTED) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.CANT_CANCEL_ACCEPTED_REQUEST,
                    LogEventReasonCode.CANT_CANCEL_ACCEPTED_REQUEST.getMessage()
                            + " - userUuid: " + authenticatedUserUuid
                            + " - requestedId: " + friend.getId(),
                    authenticatedUserUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.NOT_PROPER_STATUS_FOR_THE_REQUEST);
        }
    }
}
