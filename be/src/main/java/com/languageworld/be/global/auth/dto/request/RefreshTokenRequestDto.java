package com.languageworld.be.global.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(

        @NotBlank
        String refreshToken
){
}
