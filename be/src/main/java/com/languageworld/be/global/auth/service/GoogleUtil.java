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
                        LogEventCode.OAUTH2,
                        "FAIL",
                        LogEventReasonCode.GOOGLE_ID_TOKEN_NULL,
                        LogEventReasonCode.GOOGLE_ID_TOKEN_NULL.getMessage(),
                        null
                );

                throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
            }

            // Token의 payload
            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            // sub
            String sub = payload.getSubject();

            // profile
            String profileImageUrl = payload.get("picture").toString();

            // SocialAuthorizedUserDto
            SocialAuthorizedUser socialAuthorizedUser = SocialAuthorizedUser.of(sub, profileImageUrl);

            return socialAuthorizedUser;

        } catch (GeneralSecurityException e) {

            CustomLogger.warn(
                    LogEventCode.OAUTH2,
                    "FAIL",
                    LogEventReasonCode.INVALID_GOOGLE_TOKEN,
                    LogEventReasonCode.INVALID_GOOGLE_TOKEN.getMessage() + " - GeneralSecurityException while verifying google id token",
                    null
            );

            throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
        } catch(IOException e) {

            CustomLogger.warn(
                    LogEventCode.OAUTH2,
                    "FAIL",
                    LogEventReasonCode.INVALID_GOOGLE_TOKEN,
                    LogEventReasonCode.INVALID_TOKEN.getMessage() + " - IOExcepton while verifying google id token",
                    null
            );

            throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
        }
    }
}
