package com.languageworld.be.global.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(
        name = "LoginResponseDto",
        description = "서비스 로그인 성공 시 반환되는 JWT 토큰"
)
@Builder
public record LoginResponseDto(

        @Schema(
                description = "보호된 API 호출에 사용하는 JWT Access Token",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        String accessToken,

        @Schema(
                description = "Access Token 재발급에 사용하는 JWT Refresh Token",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
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