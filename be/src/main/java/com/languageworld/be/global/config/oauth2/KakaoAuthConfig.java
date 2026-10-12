package com.languageworld.be.global.config.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class KakaoAuthConfig {

    private final String BASE_URL = "https://kapi.kakao.com";

    @Bean
    public RestClient kakaoRestClient() {

        RestClient restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();

        return restClient;
    }

}
