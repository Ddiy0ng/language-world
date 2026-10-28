package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {

    // AUTH
    SIGNUP_SUCCESS(
            HttpStatus.CREATED,
            "USER_201",
            "회원가입에 성공했습니다."),
    LOGIN_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "로그인에 성공했습니다."),

    // MULTIPART_FILE
    FILE_UPLOAD_SUCCESS(
            HttpStatus.OK,
            "MULTIPART_FILE_200",
            "파일 업로드를 성공했습니다."),

    // TERM
    TERM_CREATED(HttpStatus.CREATED,
            "TERM_201",
            "약관 등록을 완료했습니다."),
    AGREE_TERM_SUCCESS(HttpStatus.OK,
            "TERM_200",
            "약관 동의에 성공했습니다."),
    ALL_TERM_READ_SUCCESS(HttpStatus.OK,
            "TERM_200",
            "약관 전체 조회에 성공했습니다."),
    ALL_ACTIVE_TERM_READ_SUCCESS(
            HttpStatus.OK,
            "TERM_200",
            "활성화된 약관 전체 조회에 성공했습니다."),
    NOT_AGREED_ACTIVE_TERM_READ_SUCCESS(
            HttpStatus.OK,
            "TERM_200",
            "동의하지 않은 활성화 약관 조회에 성공했습니다."),
    AGREED_TERM_READ_SUCCESS(
            HttpStatus.OK,
            "TERM_200",
            "동의한 약관 조회에 성공했습니다."),
    TERM_STATUS_UPDATE_SUCCESS(
            HttpStatus.OK,
            "TERM_200",
            "약관 활성화에 성공했습니다."),
    AGREED_TERM_DISAGREE_SUCCESS(
            HttpStatus.OK,
            "TERM_200",
            "약관 동의 철회에 성공했습니다."),

    // USER
    AUTHENTICATED_USER_INFO_READ_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "인증된 사용자 정보 조회에 성공했습니다."),
    USER_INFO_READ_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "사용자의 정보 조회에 성공했습니다."),
    USER_INFO_SUMMARY_READ_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "사용자 정보 요약 조회에 성공했습니다."),
    AUTHENTICATED_USER_INFO_UPDATE_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "인증된 사용자 정보 수정에 성공했습니다."),
    AUTHENTICATED_USER_LANGUAGE_INFO_UPDATE_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "인증된 사용자 언어 정보 수정에 성공했습니다."),
    AUTHENTICATED_USER_PASSWORD_UPDATE_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "인증된 사용자 비밀번호 수정에 성공했습니다."),
    AUTHENTICATED_USER_PROFILE_UPDATE_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "인증된 사용자  프로필 수정에 성공했습니다."),
    AUTHENTICATED_USER_REMOVE_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "인증된 사용자 탈퇴에 성공했습니다."),

    // Language
    ALL_LANGUAGE_AND_LEVEL_READ_SUCCESS(
            HttpStatus.OK,
            "LANGUAGE_200",
            "지원하는 언어와 레벨 전체 조회에 성공했습니다."),

    // User
    DELETE_SERVICE_ACCOUNT_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "서비스 계정 삭제에 성공했습니다."
    ),
    DELETE_SOCIAL_ACCOUNT_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "소셜 계정 삭제에 성공했습니다."
    ),

    // FRIEND
    ADD_FRIEND_SUCCESS(
            HttpStatus.CREATED,
            "USER_200",
            "친구 요청에 성공했습니다."),
    FIND_ALL_FRIEND_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "친구 조회에 성공했습니다."),
    ACCEPT_FRIEND_REQUEST_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "친구 요청 수락에 성공했습니다."),
    REJECT_FRIEND_REQUEST_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "친구 요청 거절에 성공했습니다."),
    CANCEL_FRIEND_REQUEST_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "친구 요청 취소에 성공했습니다."),
    REMOVE_FRIEND_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "친구 삭제에 성공했습니다."),
    FIND_SENT_FRIEND_REQUEST_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "전송한 친구 요청 조회에 성공했습니다."),
    FIND_RECEIVED_FRIEND_REQUEST(
            HttpStatus.OK,
            "USER_200",
            "요청받은 친구 요청 조회에 성공했습니다."),

    // BLOCKED_USER
    USER_BLOCK_SUCCESS(
            HttpStatus.CREATED,
            "USER_201",
            "사용자 차단에 성공했습니다."),
    ALL_BLOCKED_USER_FIND_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "차단한 사용자 조회에 성공했습니다."),
    BLOCK_CANCEL_SUCCESS(
            HttpStatus.OK,
            "USER_200",
            "사용자 차단 취소에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String customCode; //도메인명 + 상태코드
    private final String message;
}
