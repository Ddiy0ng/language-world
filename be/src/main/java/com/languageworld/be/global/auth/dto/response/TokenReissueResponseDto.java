package com.languageworld.be.global.auth.dto.response;

import lombok.Builder;

@Builder
public record TokenReissueResponseDto(

        String accessToken,

        String refreshToken
) {

    public static TokenReissueResponseDto of(String accessToken, String refreshToken) {

        TokenReissueResponseDto tokenReissueResponseDto = TokenReissueResponseDto.builder()
                .accessToken(accessToken).refreshToken(refreshToken)
                .build();

        return tokenReissueResponseDto;
    }
}
