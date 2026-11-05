package com.languageworld.be.global.auth.service;

import com.languageworld.be.global.auth.entity.RefreshToken;
import com.languageworld.be.global.auth.repository.RefreshTokenRepository;
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
public class RefreshTokenUtil {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken getRefreshToken(String tokenId) {

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByTokenId(tokenId);
        if(optionalRefreshToken.isEmpty()) {

            CustomLogger.warn(
                    LogEventCode.JWT,
                    "FAIL",
                    LogEventReasonCode.REFRESH_TOKEN_NOT_FOUND,
                    LogEventReasonCode.REFRESH_TOKEN_NOT_FOUND.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
        }
        RefreshToken refreshToken = optionalRefreshToken.get();

        return refreshToken;
    }

    public void refreshTokenOwnerCheck(UUID userUuid, UUID tokenClaimUuid, UUID dbUuid) {

        // 인증 사용자 vs token sub
        if(!userUuid.equals(dbUuid)) {

            CustomLogger.warn(
                    LogEventCode.JWT,
                    "FAIL",
                    LogEventReasonCode.NOT_TOKEN_OWNER,
                    LogEventReasonCode.NOT_TOKEN_OWNER.getMessage()
                            + " - authenticated userUuid: " + userUuid
                            + " - db token user uuid: " + dbUuid,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
        }

        // token sub vs db에 저장된 토큰 소유자의 uuid
        if(!tokenClaimUuid.equals(dbUuid)) {

            CustomLogger.warn(
                    LogEventCode.JWT,
                    "FAIL",
                    LogEventReasonCode.TOKEN_SUB_DB_UUID_NOT_SAME,
                    LogEventReasonCode.TOKEN_SUB_DB_UUID_NOT_SAME.getMessage()
                            + " - token sub uuid: " + tokenClaimUuid
                            + " - db token user uuid: " + dbUuid,
                    null
            );

            throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
        }
    }
}
