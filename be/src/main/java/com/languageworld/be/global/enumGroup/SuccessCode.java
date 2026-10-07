package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {

    LOGIN_SUCCESS(HttpStatus.OK, "USER_200", "로그인에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String customCode; //도메인명 + 상태코드
    private final String message;
}
