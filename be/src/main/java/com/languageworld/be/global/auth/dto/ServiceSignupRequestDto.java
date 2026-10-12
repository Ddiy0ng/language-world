package com.languageworld.be.global.auth.dto;

import com.languageworld.be.domain.user.enumGroup.NationCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Schema(name = "ServiceSignupRequestDto")
public record ServiceSignupRequestDto (

        @Schema(
                name = "email",
                example = "example@gmail.com"
        )
        @NotBlank
        @Email
        String email,

        @Schema(
                name = "password",
                description = "8~20글자 내 영문, 숫자, 특수문자를 포함하여야 합니다.",
                example = "password1234!"
        )
        @NotBlank
        @Size(min = 8, max = 20)
        String password,

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
){
}
