package com.languageworld.be.global.auth.dto.social;

import com.languageworld.be.domain.user.enumGroup.NationCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(name = "GoogleSignupRequestDto")
public record SocialSignupRequestDto(

        @NotBlank
        String token,

        @Schema(
                name = "name",
                example = "김철수"
        )
        @NotBlank
        @Size(min = 1, max = 50)
        String name,

        @Schema(
                name = "birth",
                example = "2000-01-01"
        )
        @NotNull
        @Past
        LocalDate birth,

        @Schema(
                name = "nation",
                example = "EN"
        )
        @NotNull
        NationCode nation,

        @Schema(
                name = "motherTongueId",
                example = "1"
        )
        @NotNull
        Long motherTongueId,

        @Schema(
                name = "learningLanguageId",
                example = "2"
        )
        @NotNull
        Long learningLanguageId,

        @Schema(
                name = "learningLanguageLevelId",
                example = "4"
        )
        @NotNull
        Long learningLanguageLevelId,

        @Schema(
                name = "serviceUseTermId",
                example = "1"
        )
        @NotNull
        Long serviceUseTermId,

        @Schema(
                name = "personalInfoUseTermId",
                example = "2"
        )
        @NotNull
        Long personalInfoUseTermId,

        @Schema(
                name = "isServiceUseTermAgreed",
                example = "true"
        )
        @NotNull
        @AssertTrue
        Boolean isServiceUseTermAgreed,

        @Schema(
                name = "isPersonalInfoUseTermAgreed",
                example = "true"
        )
        @NotNull
        @AssertTrue
        Boolean isPersonalInfoUseTermAgreed
) {
}
