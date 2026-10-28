package com.languageworld.be.domain.user.service;

import com.languageworld.be.domain.user.dto.response.BlockedUserResponseDto;
import com.languageworld.be.domain.user.entity.BlockedUser;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.repository.BlockedUserRepository;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlockedUserService {

    private final BlockedUserRepository blockedUserRepository;
    private final UserUtil userUtil;
    private final BlockedUserUtil blockedUserUtil;

    @Transactional
    public void blockUser(CustomPrincipal customPrincipal, UUID blockUuid) {

        UUID userUuid = customPrincipal.uuid();
        User authenticatedUser = userUtil.getUserByUuid(userUuid);

        // 스스로 블락 방지
        if (userUuid.equals(blockUuid)) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.CANT_BLOCK_SELF,
                    LogEventReasonCode.CANT_BLOCK_SELF.getMessage()
                            + " - authenticatedUser uuid: " + userUuid
                            + " - blockedUser uuid: " + blockUuid,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.CANT_BLOCK_SELF);
        }

        // 사용자 존재 확인
        User reqeustUser = userUtil.getUserByUuid(blockUuid);

        // 중복 블락 방지
        if (blockedUserRepository.existsByUser_UuidAndBlockedUser_Uuid(userUuid, blockUuid)) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.ALREADY_BLOCKED,
                    LogEventReasonCode.ALREADY_BLOCKED.getMessage()
                            + " - authenticatedUser uuid: " + userUuid
                            + " - blockedUser uuid: " + blockUuid,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.ALREADY_BLOCKED);
        }

        // 블락
        BlockedUser blockedUser = BlockedUser.of(authenticatedUser, reqeustUser);

        blockedUserRepository.save(blockedUser);
    }

    @Transactional
    public List<BlockedUserResponseDto> findBlockedUserList(CustomPrincipal customPrincipal) {

        //내가 차단한 리스트 조회
        UUID uuid = customPrincipal.uuid();
        List<BlockedUser> blockedUserList = blockedUserRepository.findAllByUser_Uuid(uuid);

        List<BlockedUserResponseDto> blockedUserResponseDtoList = blockedUserList.stream()
                .map(BlockedUserResponseDto::from)
                .toList();

        return blockedUserResponseDtoList;
    }

    @Transactional
    public void cancelBlock(CustomPrincipal customPrincipal, Long blockId) {

        UUID uuid = customPrincipal.uuid();
        BlockedUser blockedUser = blockedUserUtil.getBlockedUserByBlockId(uuid, blockId);
        if(!blockedUser.getUser().getUuid().equals(uuid)) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.FORBIDDEN_BLOCK_REQUEST,
                    LogEventReasonCode.FORBIDDEN_BLOCK_REQUEST.getMessage()
                            + " - authenticatedUser uuid: " + uuid
                            + " - blockId user uuid: " + blockedUser.getUser().getUuid(),
                    uuid.toString()
            );

            throw new CustomException(CustomExceptionCode.FORBIDDEN_BLOCK_REQUEST);
        }

        blockedUserRepository.delete(blockedUser);
    }
}
