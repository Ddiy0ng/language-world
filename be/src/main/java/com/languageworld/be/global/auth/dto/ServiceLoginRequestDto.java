package com.languageworld.be.global.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ServiceLoginRequestDto(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) { }
