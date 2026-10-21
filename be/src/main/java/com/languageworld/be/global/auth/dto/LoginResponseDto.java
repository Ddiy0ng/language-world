package com.languageworld.be.global.auth.dto;

import lombok.Builder;

@Builder
public record LoginResponseDto(

        String accessToken,

        String refreshToken
) {

    public static LoginResponseDto of(
            String accessToken,
            String refreshToken
    ) {
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}