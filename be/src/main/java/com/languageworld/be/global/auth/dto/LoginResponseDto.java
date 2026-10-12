package com.languageworld.be.global.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "LoginResponseDto")
@Builder
public record LoginResponseDto(

        @Schema(
                name = "accessToken",
                description = "자체 서비스에서 발급한 액세스 토큰"
        )
        String accessToken,

        @Schema(
                name = "refreshToken",
                description = "자체 서비스에서 발급한 리프레시 토큰"

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
