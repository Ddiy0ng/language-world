package com.languageworld.be.global.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoResponse(

        Long id,

        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoAccount(

            Profile profile
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Profile(

                @JsonProperty("kakao_account")
                String profileImageUrl
        ) {
        }
    }
}
