package com.languageworld.be.domain.user.service;

import com.languageworld.be.domain.user.entity.BlockedUser;
import com.languageworld.be.domain.user.repository.BlockedUserRepository;
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
public class BlockedUserUtil {

    private final BlockedUserRepository blockedUserRepository;

    protected BlockedUser getBlockedUserByBlockId(UUID authenticatedUserUuid, Long blockId) {

        Optional<BlockedUser> optionalBlockedUser = blockedUserRepository.findById(blockId);

        if(optionalBlockedUser.isEmpty()) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.BLOCKED_USER_NOT_FOUND,
                    LogEventReasonCode.BLOCKED_USER_NOT_FOUND.getMessage()
                            + " - userUuid: " + authenticatedUserUuid
                            + " - requestId: " + blockId,
                    authenticatedUserUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.BLOCKED_USER_NOT_FOUND);
        }

        return optionalBlockedUser.get();
    }

    protected void blockedUserCheck(UUID authenticatedUserUuid, UUID requestUuid) {

        if(blockedUserRepository.existsByUser_UuidAndBlockedUser_Uuid(authenticatedUserUuid, requestUuid)) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.REQUEST_FORBIDDEN_FOR_BLOCKED_USER,
                    LogEventReasonCode.REQUEST_FORBIDDEN_FOR_BLOCKED_USER.getMessage()
                            + " - userUuid: " + authenticatedUserUuid
                            + " - requestId: " + requestUuid,
                    authenticatedUserUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.REQUEST_FORBIDDEN_FOR_BLOCKED_USER);
        }
    }
}
