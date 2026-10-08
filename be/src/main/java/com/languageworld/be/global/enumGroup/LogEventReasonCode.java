package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum LogEventReasonCode {

    // AUTH
    EMAIL_ALREADY_EXIST("이미 가입된 이메일입니다."),
    EMAIL_NOT_FOUND("존재하지 않는 이메일입니다."),
    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다."),
    PASSWORD_REQUIRED("비밀번호를 입력해주세요."),
    INVALID_PASSWORD("영문, 숫자, 특수문자의 8~20자 이내의 비밀번호여야 합니다"),
    PROVIDER_ID_ALREADY_EXIST("providerId가 존재합니다."),
    EXPIRED_TOKEN("만료된 토큰입니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    TOKEN_NULL("토큰이 null입니다."),
    ACCESS_TOKEN_REQUIRED("액세스 토큰이 필요합니다."),

    // MULTIPART_FILE
    MULTIPART_FILE_REQUIRED("파일을 등록해주세요"),
    UNSUPPORTED_UPLOAD_PURPOSE_REQUEST("지원하지 않는 업로드 목적의 요청입니다."),
    UNSUPPORTED_CONTENT_TYPE("지원하지 않는 확장자입니다."),
    CONTENT_TYPE_NULL("ContentType이 null입니다."),
    PDF_REQUIRED("pdf 확장자의 파일이 아닙니다."),
    INVALID_IMAGE_TYPE("jpg, jpeg 또는 png 확장자의 이미지가 아닙니다."),
    CREATE_PDF_DIR_EXCEPTION("pdf 파일 업로드 폴더 생성 중 문제가 발생했습니다."),
    SAVE_PDF_EXCEPTION("pdf 파일 업로드 중 문제가 발생했습니다."),
    CREATE_JPEG_DIR_EXCEPTION("jpeg 파일 업로드 폴더 생성 중 문제가 발생했습니다."),
    SAVE_JPEG_EXCEPTION("jpeg 파일 업로드 중 문제가 발생했습니다."),
    CREATE_PNG_DIR_EXCEPTION("png 파일 업로드 폴더 생성 중 문제가 발생했습니다."),
    SAVE_PNG_EXCEPTION("png 파일 업로드 중 문제가 발생했습니다."),

    // TERM
    TERM_NOT_FOUND("약관을 찾을 수 없습니다."),
    TERM_ALREADY_EXIST("이미 등록된 약관입니다."),

    // LANGUAGE
    UNSUPPORTED_LANGUAGE("지원하지 않는 언어입니다."),
    LEVEL_NOT_FOUND("해당 등급이 존재하지 않습니다.");

    private final String message;
}