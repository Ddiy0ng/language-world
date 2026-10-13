package com.languageworld.be.global.auth.service;

import com.languageworld.be.global.auth.entity.KakaoResponse;
import com.languageworld.be.global.auth.entity.SocialAuthorizedUser;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class KakaoUtil {

    private final RestClient kakaoRestClient;
    private final String uri = "/v2/user/me";

    public KakaoUtil(
            @Qualifier("kakaoRestClient")
            RestClient restClient
    ) {
        kakaoRestClient = restClient;
    }

    public SocialAuthorizedUser getSocialAuthorizedUser(String accessToken) {

        SocialAuthorizedUser socialAuthorizedUser;

        try {

            KakaoResponse kakaoResponse =
                    kakaoRestClient
                            .get()
                            .uri(uri)
                            .headers(
                                    headers ->
                                            headers.setBearerAuth(accessToken)
                            )
                            .retrieve()
                            .body(KakaoResponse.class);

            if (kakaoResponse == null || kakaoResponse.id() == null) {

                // TODO: custom log 작성, CustomException 작성
                System.out.println("=====================1");
                throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
            }

            String profileImageUrl;

            if (kakaoResponse.kakaoAccount() == null) {
                // TODO: custom log 작성, CustomException 작성
                System.out.println("=====================2");
                throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
            }

            KakaoResponse.KakaoAccount kakaoAccount = kakaoResponse.kakaoAccount();

            if (kakaoResponse.kakaoAccount().profile() == null) {
                // TODO: custom log 작성, CustomException 작성
                System.out.println("=====================3");
                throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
            }

            profileImageUrl = kakaoAccount.profile().profileImageUrl();

            socialAuthorizedUser = SocialAuthorizedUser.of(
                    kakaoResponse.id().toString(),
                    profileImageUrl
            );
        } catch (HttpClientErrorException e) {

            // TODO: custom log 작성, CustomException 작성
            System.out.println("=====================4");
            System.out.println("status = " + e.getStatusCode());
            System.out.println("body = " + e.getResponseBodyAsString());
            throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
        } catch (RestClientException e) {

            // TODO: custom log 작성, CustomException 작성
            System.out.println("=====================5");
            throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
        }

        return socialAuthorizedUser;
    }
}