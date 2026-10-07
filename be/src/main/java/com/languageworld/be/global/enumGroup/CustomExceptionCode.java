package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CustomExceptionCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNDEFINED_500", "500에러:정의되지 않은 오류가 발생했습니다."),

    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "AUTH_400", "이메일 또는 비밀번호가 올바르지 않습니다."),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "AUTH_400", "이미 가입된 이메일입니다."),
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH_400", "비밀번호를 입력해주세요."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH_400", "영문, 숫자, 특수문자의 8~20자 이내의 비밀번호여야 합니다"),
    ACCOUNT_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "AUTH_400", "이미 가입된 계정입니다."),

    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "TERM_404", "약관을 찾을 수 없습니다."),
    TERM_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "TERM_400", "이미 등록된 약관입니다."),
    MULTIPART_FILE_REQUIRED(HttpStatus.BAD_REQUEST, "MULTIPART_FILE_400", "파일을 등록해주세요");

    private final HttpStatus httpStatus;
    private final String customCode; // 도메인명 + 상태코드
    private final String message;
}
