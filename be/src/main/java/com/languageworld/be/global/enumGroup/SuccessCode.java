package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {

    // AUTH
    SIGNUP_SUCCESS(HttpStatus.CREATED, "USER_201", "회원가입에 성공했습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "USER_200", "로그인에 성공했습니다."),

    // MULTIPART_FILE
    FILE_UPLOAD_SUCCESS(HttpStatus.OK, "MULTIPART_FILE_200", "파일 업로드를 성공했습니다."),

    // TERM
    TERM_CREATED(HttpStatus.CREATED, "MULTIPART_FILE_201", "약관 등록을 완료했습니다.");

    private final HttpStatus httpStatus;
    private final String customCode; //도메인명 + 상태코드
    private final String message;
}
