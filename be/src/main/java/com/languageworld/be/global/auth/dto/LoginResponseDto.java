package com.languageworld.be.global.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "서비스 자체 제공 로그인 응답 Dto")
@Builder
public record LoginResponseDto(

        @Schema(
                name = "액세스 토큰"
        )
        String accessToken,

        @Schema(
                name = "리프레시 토큰"
        )
        String refreshToken
) {

    public static LoginResponseDto of (String accessToken, String refreshToken) {

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return loginResponseDto;
    }
}
