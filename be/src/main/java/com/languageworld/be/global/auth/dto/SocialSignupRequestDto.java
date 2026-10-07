package com.languageworld.be.global.auth.dto;

import com.languageworld.be.domain.user.enumGroup.NationCode;
import com.languageworld.be.global.auth.enumGroup.SignupTypeCode;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record SocialSignupRequestDto (

        @NotNull
        SignupTypeCode signupTypeCode,

        @NotBlank
        String providerUserId,

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
        Boolean isPersonalInfoUseTermAgreed,

        @NotNull
        @AssertTrue
        Boolean isServiceUseTermAgreed
){
}
