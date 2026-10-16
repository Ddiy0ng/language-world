package com.languageworld.be.global.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.languageworld.be.global.auth.entity.SocialAuthorizedUser;
import com.languageworld.be.global.auth.enumGroup.SocialTypeCode;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
@RequiredArgsConstructor
public class GoogleUtil {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    // googleIdToken 검증
    public SocialAuthorizedUser verifyGoogleIdToken(String idTokenString) {

        try {

            // 구글 발급 토큰 확인
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idTokenString);

            // Token 공백 검증
            if(googleIdToken == null) {

                CustomLogger.warn(
                        LogEventCode.SOCIAL,
                        "FAIL",
                        LogEventReasonCode.INVALID_GOOGLE_TOKEN,
                        LogEventReasonCode.INVALID_GOOGLE_TOKEN.getMessage() + " - GoogleIdToken이 없습니다.",
                        null
                );

                throw new CustomException(CustomExceptionCode.INVALID_SOCIAL_TOKEN);
            }

            // Token의 payload
            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            // sub
            String sub = payload.getSubject();

            // sub 체크
            if(sub == null || sub.isBlank()) {

                CustomLogger.warn(
                        LogEventCode.SOCIAL,
                        "FAIL",
                        LogEventReasonCode.INVALID_GOOGLE_TOKEN,
                        LogEventReasonCode.INVALID_GOOGLE_TOKEN.getMessage() + " - Google 토큰의 subject가 없습니다.",
                        null
                );

                throw new CustomException(CustomExceptionCode.INVALID_SOCIAL_TOKEN);
            }

            // profile
            String profileImageUrl = null;
            if(payload.get("picture") != null)
                    profileImageUrl = payload.get("picture").toString();

            // SocialAuthorizedUserDto
            SocialAuthorizedUser socialAuthorizedUser = SocialAuthorizedUser.of(sub, profileImageUrl);

            return socialAuthorizedUser;

        } catch (GeneralSecurityException e) {

            CustomLogger.warn(
                    LogEventCode.SOCIAL,
                    "FAIL",
                    LogEventReasonCode.SOCIAL_TOKEN_VERIFICATION_FAILED,
                    LogEventReasonCode.SOCIAL_TOKEN_VERIFICATION_FAILED.getMessage() + " - provider: Google",
                    null
            );

            throw new CustomException(CustomExceptionCode.INVALID_SOCIAL_TOKEN);
        } catch(IOException e) {

            CustomLogger.warn(
                    LogEventCode.SOCIAL,
                    "FAIL",
                    LogEventReasonCode.SOCIAL_API_REQUEST_FAILED,
                    LogEventReasonCode.SOCIAL_API_REQUEST_FAILED.getMessage() + " - provider: Google",
                    null
            );

            throw new CustomException(CustomExceptionCode.SOCIAL_API_REQUEST_FAILED);
        }
    }
}
