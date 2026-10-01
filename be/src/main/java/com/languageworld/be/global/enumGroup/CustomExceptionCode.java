package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CustomExceptionCode {

    CUSTOM_EXCEPTION_CODE(HttpStatus.BAD_REQUEST, "USER_400", "example message");

    private final HttpStatus httpStatus;
    private final String customCode; // 도메인명 + 상태코드
    private final String message;
}
