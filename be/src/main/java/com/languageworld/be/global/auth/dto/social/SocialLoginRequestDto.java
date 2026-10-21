package com.languageworld.be.global.auth.dto.social;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequestDto (

        @NotBlank
        String token
)
{ }
