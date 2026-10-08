package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CustomExceptionCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNDEFINED_500", "500에러:정의되지 않은 오류가 발생했습니다."),

    // AUTH
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "AUTH_400", "이메일 또는 비밀번호가 올바르지 않습니다."),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "AUTH_400", "이미 가입된 이메일입니다."),
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH_400", "비밀번호를 입력해주세요."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH_400", "영문, 숫자, 특수문자의 8~20자 이내의 비밀번호여야 합니다"),
    ACCOUNT_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "AUTH_400", "이미 가입된 계정입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401", "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401", "유효하지 않은 토큰입니다."),

    // MULTIPART_FILE
    MULTIPART_FILE_REQUIRED(HttpStatus.BAD_REQUEST, "MULTIPART_FILE_400", "파일을 등록해주세요"),
    INVALID_FILE_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "MULTIPART_FILE_415", "잘못된 형식의 파일입니다."),
    CREATE_DIR_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "MULTIPART_FILE_500", "파일 업로드 폴더 생성 중 문제가 발생했습니다."),
    SAVE_FILE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "MULTIPART_FILE_500", "파일 업로드 중 문제가 발생했습니다."),

    // TERM
    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "TERM_404", "약관을 찾을 수 없습니다."),
    TERM_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "TERM_400", "이미 등록된 약관입니다."),

    // LANGUAGE
    UNSUPPORTED_LANGUAGE(HttpStatus.BAD_REQUEST, "LANGUAGE_400", "지원하지 않는 언어입니다."),
    LEVEL_NOT_FOUND(HttpStatus.BAD_REQUEST, "LANGUAGE_400", "해당 등급이 존재하지 않습니다."),
    NOT_PROPER_LEVEL_FOR_LANGUAGE(HttpStatus.BAD_REQUEST, "LANGUAGE_400", "해당 언어에서 제공하는 등급이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String customCode; // 도메인명 + 상태코드
    private final String message;
}
