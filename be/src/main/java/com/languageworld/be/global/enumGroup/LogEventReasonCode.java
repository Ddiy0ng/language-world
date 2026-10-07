package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum LogEventReasonCode {

    EMAIL_ALREADY_EXIST("이미 가입된 이메일입니다."),
    EMAIL_NOT_FOUND("존재하지 않는 이메일입니다."),
    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다."),
    PASSWORD_REQUIRED("비밀번호를 입력해주세요."),
    INVALID_PASSWORD("영문, 숫자, 특수문자의 8~20자 이내의 비밀번호여야 합니다"),
    PROVIDER_ID_ALREADY_EXIST("providerId가 존재합니다."),
    TERM_NOT_FOUND("약관을 찾을 수 없습니다."),
    TERM_ALREADY_EXIST("이미 등록된 약관입니다."),
    MULTIPART_FILE_REQUIRED("파일을 등록해주세요");

    private final String message;
}