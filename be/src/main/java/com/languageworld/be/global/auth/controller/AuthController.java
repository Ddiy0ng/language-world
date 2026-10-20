package com.languageworld.be.global.auth.controller;

import com.languageworld.be.global.auth.dto.*;
import com.languageworld.be.global.auth.dto.social.SocialSignupRequestDto;
import com.languageworld.be.global.auth.dto.social.SocialLoginRequestDto;
import com.languageworld.be.global.auth.service.AuthService;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "AUTH",
        description = "자체 및 소셜 회원가입·로그인 API"
)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            operationId = "signupService",
            summary = "자체 회원가입",
            description = """
                    이메일과 비밀번호를 이용하여 회원가입합니다.

                    사용자 정보와 현재 유효한 약관의 ID 및
                    필수 약관 동의 여부를 전달해야 합니다.

                    회원가입 성공 시 별도의 토큰을 반환하지 않습니다.
                    """
    )
    @PostMapping(
            value = "/signup/service",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody
            ServiceSignupRequestDto requestDto
    ) {

        authService.signup(requestDto);

        return ResponseEntityUtil.success(
                SuccessCode.SIGNUP_SUCCESS
        );
    }

    @Operation(
            operationId = "loginService",
            summary = "자체 로그인",
            description = """
                    이메일과 비밀번호를 검증하여 로그인합니다.

                    로그인에 성공하면 서비스 자체 JWT인
                    Access Token과 Refresh Token을 반환합니다.
                    """
    )
    @PostMapping(
            value = "/login/service",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
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

    @Operation(
            operationId = "signupSocial",
            summary = "소셜 회원가입",
            description = """
                    Google 또는 Kakao에서 발급받은 토큰을
                    백엔드에서 검증한 후 회원가입합니다.

                    사용자 정보와 필수 약관 동의 정보도 전달합니다.

                    토큰 검증 및 중복 가입 검사를 수행합니다.
                    """
    )
    @PostMapping(
            value = "/signup/{socialSignupType}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> signup(

            @Parameter(
                    description = "소셜 인증 제공자",
                    example = "google",
                    schema = @Schema(
                            allowableValues = {"google", "kakao"}
                    )
            )
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

    @Operation(
            operationId = "loginSocial",
            summary = "소셜 로그인",
            description = """
                    Google 또는 Kakao에서 발급받은 토큰을 검증합니다.

                    소셜 계정이 서비스에 가입된 경우,
                    자체 JWT Access Token과 Refresh Token을 반환합니다.

                    서비스에 가입되지 않은 소셜 계정은
                    로그인에 실패합니다.
                    """
    )
    @PostMapping(
            value = "/login/{socialLoginType}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(

            @Parameter(
                    description = "소셜 인증 제공자",
                    example = "google",
                    schema = @Schema(
                            allowableValues = {"google", "kakao"}
                    )
            )
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
}