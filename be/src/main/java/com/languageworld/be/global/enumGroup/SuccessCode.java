package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {

    SUCCESS_CODE_EXAMPLE(HttpStatus.OK, "USER_200", "example message");

    private final HttpStatus httpStatus;
    private final String customCode; //도메인명 + 상태코드
    private final String message;
}
