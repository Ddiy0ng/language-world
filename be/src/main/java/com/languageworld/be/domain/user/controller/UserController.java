package com.languageworld.be.domain.user.controller;

import com.languageworld.be.domain.user.dto.request.*;
import com.languageworld.be.domain.user.dto.response.UserInfoResponseDto;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import com.languageworld.be.domain.user.service.UserService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // TODO 인증된 사용자 정보 상세 조회: 프로필 이미지, 이름,  국적, 모국어, 학습어 + 레벨, 바이오
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> findAuthenticatedUserInfo(@AuthenticationPrincipal CustomPrincipal customPrincipal) {

        // customPrincipal
        UserInfoResponseDto userInfoResponseDto = userService.findUserInfo(customPrincipal.uuid());

        return ResponseEntityUtil.success(SuccessCode.AUTHENTICATED_USER_INFO_READ_SUCCESS, userInfoResponseDto);
    }

    // TODO 회원 정보 조회(키워드 사용해 조건 검색 또는 전체 - 요약 조회): 프로필 이미지, 이름, 국적, 학습어 + 레벨    // 자기 자신은 검색에서 제외
    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserSummaryResponseDto>>> searchUserSummaryList(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                                           @RequestParam(required = false) String keyword,
                                                                                           @RequestParam(required = false) Long learningLanguageId,
                                                                                           @RequestParam(required = false) Long learningLanguageLevelId) {

        List<UserSummaryResponseDto> userSummaryResponseDtoList = userService.searchUserSummaryList(customPrincipal, keyword, learningLanguageId, learningLanguageLevelId);

        return ResponseEntityUtil.success(SuccessCode.USER_INFO_SUMMARY_READ_SUCCESS, userSummaryResponseDtoList);
    }

    // TODO 회원 정보 상세 조회(단건): 프로필 이미지, 이름, 국적, 모국어, 학습어 + 레벨, 바이오
    @GetMapping("/{userUuid}")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> findUserInfo(@PathVariable UUID userUuid) {

        // by uuid
        UserInfoResponseDto userInfoResponseDto = userService.findUserInfo(userUuid);

        return ResponseEntityUtil.success(SuccessCode.USER_INFO_READ_SUCCESS, userInfoResponseDto);
    }

    // TODO 회원 정보 수정: 이름, 국적, 바이오, 모국어
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Void>> changeUserInfo(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                            @RequestBody ChangeUserInfoRequestDto changeUserInfoRequestDto) {

        userService.changeUserInfo(customPrincipal, changeUserInfoRequestDto);

        return ResponseEntityUtil.success(SuccessCode.AUTHENTICATED_USER_INFO_UPDATE_SUCCESS);
    }

    // TODO 학습어 관련 정보 변경
    @PatchMapping("/me/language")
    public ResponseEntity<ApiResponse<Void>> changeUserLanguageInfo(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                    @RequestBody ChangeUserLanguageInfoRequestDto changeUserLanguageInfoRequestDto) {

        userService.changeUserLanguageInfo(customPrincipal, changeUserLanguageInfoRequestDto);

        return ResponseEntityUtil.success(SuccessCode.AUTHENTICATED_USER_LANGUAGE_INFO_UPDATE_SUCCESS);
    }

    // TODO 회원 비밀번호 수정
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changeUserPassword(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                            @RequestBody ChangeUserPasswordRequestDto changeUserPasswordRequestDto) {

        userService.changeUserPassword(customPrincipal, changeUserPasswordRequestDto);

        return ResponseEntityUtil.success(SuccessCode.AUTHENTICATED_USER_PASSWORD_UPDATE_SUCCESS);
    }

    // TODO 프로필 수정(MultipartFile)
    @PatchMapping(
            value = "/me/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> changeUserProfile(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                               @RequestPart("file") MultipartFile multipartFile) {

        userService.changeUserProfile(customPrincipal, multipartFile);

        return ResponseEntityUtil.success(SuccessCode.AUTHENTICATED_USER_PROFILE_UPDATE_SUCCESS);
    }

    // TODO 인증된 사용자 회원 탈퇴(SoftDelete)
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> removeUser(@AuthenticationPrincipal CustomPrincipal customPrincipal) {

        userService.removeUser(customPrincipal);

        return ResponseEntityUtil.success(SuccessCode.AUTHENTICATED_USER_REMOVE_SUCCESS);
    }

}
