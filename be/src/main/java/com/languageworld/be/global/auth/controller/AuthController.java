package com.languageworld.be.global.auth.controller;

import com.languageworld.be.global.auth.dto.*;
import com.languageworld.be.global.auth.dto.social.SocialSignupRequestDto;
import com.languageworld.be.global.auth.dto.social.SocialLoginRequestDto;
import com.languageworld.be.global.auth.service.AuthService;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup/service")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody
            ServiceSignupRequestDto requestDto
    ) {

        authService.signup(requestDto);

        return ResponseEntityUtil.success(
                SuccessCode.SIGNUP_SUCCESS
        );
    }

    @PostMapping("/login/service")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody
            ServiceLoginRequestDto requestDto
    ) {

        LoginResponseDto responseDto =
                authService.login(requestDto);

        return ResponseEntityUtil.success(
                SuccessCode.LOGIN_SUCCESS,
                responseDto
        );
    }

    @PostMapping("/signup/{socialSignupType}")
    public ResponseEntity<ApiResponse<Void>> signup(

            @PathVariable
            String socialSignupType,

            @Valid @RequestBody
            SocialSignupRequestDto requestDto
    ) {

        authService.signup(
                socialSignupType,
                requestDto
        );

        return ResponseEntityUtil.success(
                SuccessCode.SIGNUP_SUCCESS
        );
    }

    @PostMapping("/login/{socialLoginType}")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(

            @PathVariable
            String socialLoginType,

            @Valid @RequestBody
            SocialLoginRequestDto requestDto
    ) {

        LoginResponseDto responseDto =
                authService.login(
                        socialLoginType,
                        requestDto
                );

        return ResponseEntityUtil.success(
                SuccessCode.LOGIN_SUCCESS,
                responseDto
        );
    }

    // TODO 회원탈퇴: user의 식별정보 null, 참조 객체 삭제(account, agreedTerm 등)
}