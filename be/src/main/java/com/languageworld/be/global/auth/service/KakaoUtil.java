package com.languageworld.be.global.auth.service;

import com.languageworld.be.global.auth.entity.KakaoResponse;
import com.languageworld.be.global.auth.entity.SocialAuthorizedUser;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class KakaoUtil {

    private final RestClient kakaoRestClient;

    private static final String USER_INFO_URI =
            "/v2/user/me";

    public KakaoUtil(
            @Qualifier("kakaoRestClient")
            RestClient restClient
    ) {
        this.kakaoRestClient = restClient;
    }

    public SocialAuthorizedUser getSocialAuthorizedUser(String accessToken) {

        try {

            KakaoResponse kakaoResponse =
                    kakaoRestClient
                            .get()
                            .uri(USER_INFO_URI)
                            .headers(headers -> headers.setBearerAuth(accessToken))
                            .retrieve()
                            .body(KakaoResponse.class);

            // Kakao 사용자 식별정보 확인
            if (kakaoResponse == null || kakaoResponse.id() == null) {

                CustomLogger.warn(
                        LogEventCode.SOCIAL,
                        "FAIL",
                        LogEventReasonCode.INVALID_KAKAO_RESPONSE,
                        LogEventReasonCode.INVALID_KAKAO_RESPONSE
                                .getMessage() + " - KakaoResponse 또는 user id가 없습니다.",
                        null
                );

                throw new CustomException(
                        CustomExceptionCode.INVALID_KAKAO_RESPONSE
                );
            }

            String profileImageUrl = null;
            if (kakaoResponse.kakaoAccount() != null && kakaoResponse.kakaoAccount().profile() != null)
                profileImageUrl = kakaoResponse.kakaoAccount().profile().profileImageUrl();

            return SocialAuthorizedUser.of(
                    kakaoResponse.id().toString(),
                    profileImageUrl
            );

        } catch (HttpClientErrorException e) {

            CustomLogger.warn(
                    LogEventCode.SOCIAL,
                    "FAIL",
                    LogEventReasonCode.INVALID_SOCIAL_TOKEN,
                    LogEventReasonCode.INVALID_SOCIAL_TOKEN.getMessage() + " - provider: KAKAO",
                    null
            );

            throw new CustomException(
                    CustomExceptionCode.INVALID_SOCIAL_TOKEN
            );

        } catch (RestClientException e) {

            CustomLogger.warn(
                    LogEventCode.SOCIAL,
                    "FAIL",
                    LogEventReasonCode.SOCIAL_API_REQUEST_FAILED,
                    LogEventReasonCode.SOCIAL_API_REQUEST_FAILED.getMessage() + " - provider: KAKAO",
                    null
            );

            throw new CustomException(
                    CustomExceptionCode.SOCIAL_API_REQUEST_FAILED
            );
        }
    }
}