package com.languageworld.be.global.auth.dto;

import com.languageworld.be.domain.user.enumGroup.NationCode;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ServiceSignupRequestDto (

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 20)
        String password,

        @NotBlank
        @Size(min = 1, max = 50)
        String name,

        @NotNull
        @Past
        LocalDate birth,

        @NotNull
        NationCode nation,

        @NotNull
        Long motherTongueId,

        @NotNull
        Long learningLanguageId,

        @NotNull
        Long learningLanguageLevelId,

        @NotNull
        Long serviceUseTermId,

        @NotNull
        Long personalInfoUseTermId,

        @NotNull
        @AssertTrue
        Boolean isServiceUseTermAgreed,

        @NotNull
        @AssertTrue
        Boolean isPersonalInfoUseTermAgreed
){
}
