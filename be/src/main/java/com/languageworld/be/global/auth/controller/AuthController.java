package com.languageworld.be.global.auth.controller;

import com.languageworld.be.global.auth.dto.LoginResponseDto;
import com.languageworld.be.global.auth.dto.ServiceLoginRequestDto;
import com.languageworld.be.global.auth.dto.ServiceSignupRequestDto;
import com.languageworld.be.global.auth.service.AuthService;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    encoding = {
                            @Encoding(
                                    name = "json",
                                    contentType = MediaType.APPLICATION_JSON_VALUE
                            )
                    }
            )
    )
    @PostMapping("/signup/service")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody ServiceSignupRequestDto serviceSignupRequestDto) {

        authService.signup(serviceSignupRequestDto);

        return ResponseEntityUtil.success(SuccessCode.SIGNUP_SUCCESS);
    }

//    @PostMapping("/signup/social")
//    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SocialSignupRequestDto socialSignupRequestDto) {
//
//        authService.signup(socialSignupRequestDto);
//
//        return ResponseEntityUtil.success(SuccessCode.SIGNUP_SUCCESS);
//    }

    @Operation(
            summary = "서비스 자체 제공 로그인",
            description = "사용자가 이메일과, 비밀번호를 직접 입력하여 로그인하는 일반 로그인 API 입니다." +
                    "로그인 시 AccessToken과 RefreshToken이 반환됩니다."
    )
    @PostMapping("/login/service")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody ServiceLoginRequestDto serviceLoginRequestDto) {

        LoginResponseDto loginResponseDto = authService.login(serviceLoginRequestDto);

        return ResponseEntityUtil.success(SuccessCode.LOGIN_SUCCESS, loginResponseDto);
    }
//
//    @PostMapping("/login/social")
//    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
//
//        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
//
//        return ResponseEntityUtil.success(SuccessCode.LOGIN_SUCCESS, loginResponseDto);
//    }
}
