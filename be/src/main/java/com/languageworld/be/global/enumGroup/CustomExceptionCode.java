package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CustomExceptionCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNDEFINED_500", "500에러:정의되지 않은 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String customCode; // 도메인명 + 상태코드
    private final String message;
}
