package com.languageworld.be.global.auth.controller;

import com.languageworld.be.global.auth.dto.*;
import com.languageworld.be.global.auth.dto.social.SocialSignupRequestDto;
import com.languageworld.be.global.auth.dto.social.SocialLoginRequestDto;
import com.languageworld.be.global.auth.service.AuthService;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "AUTH",
        description = "회원가입, 로그인 API 입니다."
)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "서비스 자체 제공 회원가입",
            description = "사용자가 이메일과, 비밀번호를 직접 입력하여 가입하는 일반 회원가입 API 입니다."
    )
    @PostMapping(value = "/signup/service",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody ServiceSignupRequestDto serviceSignupRequestDto) {

        authService.signup(serviceSignupRequestDto);

        return ResponseEntityUtil.success(SuccessCode.SIGNUP_SUCCESS);
    }

    @Operation(
            summary = "서비스 자체 제공 로그인",
            description = "사용자가 이메일과, 비밀번호를 직접 입력하여 로그인하는 일반 로그인 API 입니다." +
                    "로그인 시 AccessToken과 RefreshToken이 반환됩니다."
    )
    @PostMapping(value = "/login/service",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody ServiceLoginRequestDto serviceLoginRequestDto) {

        LoginResponseDto loginResponseDto = authService.login(serviceLoginRequestDto);

        return ResponseEntityUtil.success(SuccessCode.LOGIN_SUCCESS, loginResponseDto);
    }


    @Operation(
            summary = "소셜 회원가입",
            description = "소셜 측에서 발급한 토큰을 검증하고 자체 서비스에 회원가입을 진행하는 api입니다.."
    )
    @PostMapping(value = "/signup/{socialSignupType}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> signup(@PathVariable String socialSignupType, @Valid @RequestBody SocialSignupRequestDto socialSignupRequestDto) {

        authService.signup(socialSignupType, socialSignupRequestDto);

        return ResponseEntityUtil.success(SuccessCode.SIGNUP_SUCCESS);
    }

    @Operation(
            summary = "Google 로그인",
            description = "Google을 이용해 서비스에 로그인을 진행하는 api입니다." +
                    "로그인 시 자체 서비스의 AccessToken과 RefreshToken이 반환됩니다."
    )
    @PostMapping(value = "/login/google",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@PathVariable String loginTypeCode, @Valid @RequestBody SocialLoginRequestDto socialLoginRequestDto) {

        LoginResponseDto loginResponseDto = authService.login(loginTypeCode, socialLoginRequestDto);

        return ResponseEntityUtil.success(SuccessCode.LOGIN_SUCCESS, loginResponseDto);
    }
}
