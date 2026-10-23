package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {

    // AUTH
    SIGNUP_SUCCESS(
            HttpStatus.CREATED,
            "USER_201",
            "회원가입에 성공했습니다."),
    LOGIN_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "로그인에 성공했습니다."),

    // MULTIPART_FILE
    FILE_UPLOAD_SUCCESS(
            HttpStatus.OK,
            "MULTIPART_FILE_200",
            "파일 업로드를 성공했습니다."),

    // TERM
    TERM_CREATED(HttpStatus.CREATED,
            "TERM_201",
            "약관 등록을 완료했습니다."),
    AGREE_TERM_SUCCESS(HttpStatus.OK,
            "TERM_200",
            "약관 동의에 성공했습니다."),
    ALL_TERM_READ_SUCCESS(HttpStatus.OK,
            "TERM_200",
            "약관 전체 조회에 성공했습니다."),
    ALL_ACTIVE_TERM_READ_SUCCESS(
            HttpStatus.OK,
            "TERM_200",
            "활성화된 약관 전체 조회에 성공했습니다."),
    NOT_AGREED_ACTIVE_TERM_READ_SUCCESS(
            HttpStatus.OK,
            "TERM_200",
            "동의하지 않은 활성화 약관 조회에 성공했습니다."),
    AGREED_TERM_READ_SUCCESS(
            HttpStatus.OK,
            "TERM_200",
            "동의한 약관 조회에 성공했습니다."),
    TERM_STATUS_UPDATE_SUCCESS(
            HttpStatus.OK,
            "TERM_200",
            "약관 활성화에 성공했습니다."),
    AGREED_TERM_DISAGREE_SUCCESS(
            HttpStatus.OK,
            "TERM_200",
            "약관 동의 철회에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String customCode; //도메인명 + 상태코드
    private final String message;
}
