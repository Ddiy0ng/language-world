package com.languageworld.be.global.auth.controller;

import com.languageworld.be.global.auth.dto.request.*;
import com.languageworld.be.global.auth.dto.response.LoginResponseDto;
import com.languageworld.be.global.auth.dto.response.TokenReissueResponseDto;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.auth.service.AuthService;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup/service")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody ServiceSignupRequestDto requestDto) {

        authService.signup(requestDto);

        return ResponseEntityUtil.success(
                SuccessCode.SIGNUP_SUCCESS
        );
    }

    @PostMapping("/login/service")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody ServiceLoginRequestDto requestDto) {

        LoginResponseDto responseDto = authService.login(requestDto);

        return ResponseEntityUtil.success(SuccessCode.LOGIN_SUCCESS, responseDto);
    }

    @PostMapping("/signup/{socialSignupType}")
    public ResponseEntity<ApiResponse<Void>> signup(@PathVariable String socialSignupType,
                                                    @Valid @RequestBody SocialSignupRequestDto requestDto) {

        authService.signup(socialSignupType, requestDto);

        return ResponseEntityUtil.success(SuccessCode.SIGNUP_SUCCESS);
    }

    @PostMapping("/login/{socialLoginType}")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@PathVariable String socialLoginType,
                                                               @Valid @RequestBody SocialLoginRequestDto requestDto) {

        LoginResponseDto responseDto = authService.login(socialLoginType, requestDto);

        return ResponseEntityUtil.success(SuccessCode.LOGIN_SUCCESS, responseDto);
    }


    // TODO 액세스 토큰 재발급: 액세스 만료 시 리프레시 검증 후 액세스 재발급(토큰 검증, 계정 검증, 액세스 발급, 리프레시는 경우 따라 재발급, 토큰 반환), 이전 리프레시 삭제
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenReissueResponseDto>> reissueToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {

        TokenReissueResponseDto tokenReissueResponseDto = authService.reissueToken(refreshTokenRequestDto);

        return ResponseEntityUtil.success(SuccessCode.TOKEN_REISSUE_SUCCESS, tokenReissueResponseDto);
    }

    // TODO 로그아웃: 리프레시 삭제
    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                    @Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {

        authService.logout(customPrincipal, refreshTokenRequestDto);

        return ResponseEntityUtil.success(SuccessCode.LOGOUT_SUCCESS);
    }


    // TODO 회원탈퇴: user의 식별정보 null, 참조 객체 삭제(account, agreedTerm 등), 모든 액세스, 리프레시 삭제
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> removeUser(@AuthenticationPrincipal CustomPrincipal customPrincipal) {

        authService.removeUser(customPrincipal);

        return ResponseEntityUtil.success(SuccessCode.USER_REMOVE_SUCCESS);
    }

}