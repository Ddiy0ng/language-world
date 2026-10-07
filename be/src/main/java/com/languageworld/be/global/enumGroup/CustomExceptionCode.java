package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CustomExceptionCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNDEFINED_500", "500에러:정의되지 않은 오류가 발생했습니다."),

    // 반환용 응답
    LOGIN_FAILED(HttpStatus.NOT_FOUND, "AUTH_404", "이메일 또는 비밀번호가 올바르지 않습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String customCode; // 도메인명 + 상태코드
    private final String message;
}
