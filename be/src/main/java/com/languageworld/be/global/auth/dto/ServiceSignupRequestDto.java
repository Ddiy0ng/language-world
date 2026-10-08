package com.languageworld.be.global.auth.dto;

import com.languageworld.be.domain.user.enumGroup.NationCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Schema(name = "서비스 자체 제공 회원가입 요청 Dto")
public record ServiceSignupRequestDto (

        @Schema(
                name = "이메일",
                example = "example@gmail.com"
        )
        @NotBlank
        @Email
        String email,

        @Schema(
                name = "비밀번호",
                description = "8~20글자 내 영문, 숫자, 특수문자를 포함하여야 합니다.",
                example = "password1234!"
        )
        @NotBlank
        @Size(min = 8, max = 20)
        String password,

        @Schema(
                name = "이름",
                example = "김철수"
        )
        @NotBlank
        @Size(min = 1, max = 50)
        String name,

        @Schema(
                name = "생년월일",
                example = "2000-01-01"
        )
        @NotNull
        @Past
        LocalDate birth,

        @Schema(
                name = "국적",
                example = "EN"
        )
        @NotNull
        NationCode nation,

        @Schema(
                name = "모국어 Id",
                example = "1"
        )
        @NotNull
        Long motherTongueId,

        @Schema(
                name = "학습어 Id",
                example = "2"
        )
        @NotNull
        Long learningLanguageId,

        @Schema(
                name = "학습어 수준 Id",
                example = "4"
        )
        @NotNull
        Long learningLanguageLevelId,

        @Schema(
                name = "서비스 이용약관 Id",
                example = "1"
        )
        @NotNull
        Long serviceUseTermId,

        @Schema(
                name = "개인정보 이용약관 Id",
                example = "2"
        )
        @NotNull
        Long personalInfoUseTermId,

        @Schema(
                name = "서비스 이용약관 동의 여부",
                example = "true"
        )
        @NotNull
        @AssertTrue
        Boolean isServiceUseTermAgreed,

        @Schema(
                name = "개인정보 이용약관 동의 여부",
                example = "true"
        )
        @NotNull
        @AssertTrue
        Boolean isPersonalInfoUseTermAgreed
){
}
