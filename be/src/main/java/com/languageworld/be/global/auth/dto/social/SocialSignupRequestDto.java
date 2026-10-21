package com.languageworld.be.global.auth.dto.social;

import com.languageworld.be.domain.user.enumGroup.NationCode;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record SocialSignupRequestDto(

        @NotBlank
        String token,

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
) {
}
