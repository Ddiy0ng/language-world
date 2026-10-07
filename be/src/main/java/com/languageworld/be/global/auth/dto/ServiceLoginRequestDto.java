package com.languageworld.be.global.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "서비스 자체 제공 로그인 요청 Dto")
public record ServiceLoginRequestDto(

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
        String password
) { }
