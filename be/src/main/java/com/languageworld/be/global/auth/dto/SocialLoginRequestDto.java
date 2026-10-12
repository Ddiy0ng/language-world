package com.languageworld.be.global.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "SocialLoginRequestDto")
public record SocialLoginRequestDto (

        @Schema(
                name = "idToken"
        )
        @NotBlank
        String idToken
)
{ }
