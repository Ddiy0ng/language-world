package com.languageworld.be.global.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CustomExceptionCode {

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "UNDEFINED_500",
            "500에러:정의되지 않은 오류가 발생했습니다."),

    // AUTH
    UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "AUTH_401",
            "인증되지 않은 사용자입니다."),
    UNSUPPORTED_USER_ROLE_TYPE(
            HttpStatus.BAD_REQUEST,
            "AUTH_401",
            "지원하지 않는 권한입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN,
            "AUTH_403",
            "권한이 없습니다."),
    ADMIN_ACCESS_REQUIRED(
            HttpStatus.FORBIDDEN,
            "AUTH_403",
            "관리자 권한이 없습니다."),
    DELETED_USER(
            HttpStatus.NOT_FOUND,
            "AUTH_404",
            "탈퇴한 사용자입니다."),
    LOGIN_FAILED(
            HttpStatus.BAD_REQUEST,
            "AUTH_400",
            "이메일 또는 비밀번호가 올바르지 않습니다."),
    EMAIL_ALREADY_EXIST(
            HttpStatus.BAD_REQUEST,
            "AUTH_400",
            "이미 가입된 이메일입니다."),
    PASSWORD_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "AUTH_400",
            "비밀번호를 입력해주세요."),
    INVALID_PASSWORD(
            HttpStatus.BAD_REQUEST,
            "AUTH_400",
            "영문, 숫자, 특수문자의 8~20자 이내의 비밀번호여야 합니다"),
    ACCOUNT_ALREADY_EXIST(
            HttpStatus.BAD_REQUEST,
            "AUTH_400",
            "이미 가입된 계정입니다."),
    ACCOUNT_NOT_EXIST(
            HttpStatus.BAD_REQUEST,
            "AUTH_400", "존재하지 않는 계정입니다."),
    EXPIRED_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH_401",
            "만료된 토큰입니다."),
    INVALID_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH_401",
            "유효하지 않은 토큰입니다."),

    // SOCIAL
    UNSUPPORTED_SOCIAL_TYPE(
            HttpStatus.BAD_REQUEST,
            "SOCIAL_400",
            "지원하지 않는 소셜 타입입니다."),
    INVALID_SOCIAL_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "SOCIAL_401",
            "유효하지 않은 소셜 인증 토큰입니다."),
    INVALID_KAKAO_RESPONSE(
            HttpStatus.BAD_GATEWAY,
            "SOCIAL_502",
            "카카오 인증 서버의 응답이 유효하지 않습니다."),
    SOCIAL_API_REQUEST_FAILED(HttpStatus.BAD_GATEWAY,
            "SOCIAL_502",
            "소셜 인증 서버와 통신하는 중 문제가 발생했습니다."),

    // MULTIPART_FILE
    MULTIPART_FILE_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "MULTIPART_FILE_400",
            "파일을 등록해주세요"),
    INVALID_FILE_TYPE(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "MULTIPART_FILE_415",
            "잘못된 형식의 파일입니다."),
    CREATE_DIR_EXCEPTION(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "MULTIPART_FILE_500",
            "파일 업로드 폴더 생성 중 문제가 발생했습니다."),
    SAVE_FILE_EXCEPTION(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "MULTIPART_FILE_500",
            "파일 업로드 중 문제가 발생했습니다."),
    UNSUPPORTED_UPLOAD_PURPOSE_REQUEST(HttpStatus.BAD_REQUEST,
            "MULTIPART_FILE_400",
            "지원하지 않는 파일 업로드 목적입니다."),
    MULTIPART_FILE_DELETE_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "MULTIPART_FILE_500",
            "파일 삭제에 실패했습니다."),
    FILE_TRANSACTION_NOT_ACTIVE(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "MULTIPART_FILE_500",
            "현재 실행 중인 스레드에 DB 트랜잭션이 활성화되어있지 않습니다."),

    // TERM
    TERM_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "TERM_404",
            "약관을 찾을 수 없습니다."),
    AGREED_TERM_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "TERM_404",
            "동의한 약관을 찾을 수 없습니다."),
    TERM_ALREADY_EXIST(
            HttpStatus.BAD_REQUEST,
            "TERM_400",
            "이미 등록된 약관입니다."),
    NOT_AGREED_TERM_OWNER(
            HttpStatus.UNAUTHORIZED,
            "TERM_401",
            "본인이 동의한 약관이 아닙니다."),
    REQUIRED_TERM_NOT_AGREED(
            HttpStatus.BAD_REQUEST,
            "TERM_400",
            "활성화되지 않은 약관입니다."),
    UNMATCHED_TERM_PURPOSE(
            HttpStatus.BAD_REQUEST,
            "TERM_400",
            "목적과 요청 약관의 목적이 일치하지 않습니다."),

    // LANGUAGE
    UNSUPPORTED_LANGUAGE(
            HttpStatus.BAD_REQUEST,
            "LANGUAGE_400",
            "지원하지 않는 언어입니다."),
    LEVEL_NOT_FOUND(
            HttpStatus.BAD_REQUEST,
            "LANGUAGE_400",
            "해당 등급이 존재하지 않습니다."),
    NOT_PROPER_LEVEL_FOR_LANGUAGE(
            HttpStatus.BAD_REQUEST,
            "LANGUAGE_400",
            "해당 언어에서 제공하는 등급이 아닙니다."),

    // USER
    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER_404",
            "존재하지 않는 사용자입니다."),
    LEARNING_LANGUAGE_AND_LEVEL_BOTH_REQUIRE(
            HttpStatus.BAD_REQUEST,
            "USER_400",
            "학습어와 레벨 모두 입력해야 합니다."),
    NOT_SUPPORTED__FOR_SOCIAL_USER(
            HttpStatus.BAD_REQUEST,
            "USER_400",
            "소셜 사용자에게 지원하지 않는 기능입니다."),

    // FRIEND
    CANT_ADD_SELF_AS_FRIEND(
            HttpStatus.BAD_REQUEST,
            "USER_400",
            "스스로 친구추가할 수 없습니다."),
    ALREADY_FRIEND(
            HttpStatus.BAD_REQUEST,
            "USER_400",
            "이미 친구추가된 사용자입니다."),
    RECEIVED_FRIEND_REQUEST_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER_404",
            "받은 친구 요청이 없습니다."),
    SENT_FRIEND_REQUEST_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER_404",
            "전송한 친구 요청이 없습니다."),
    ACCEPTED_FRIEND_REQUEST_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER_404",
            "수락한 친구 요청이 없습니다."),
    NOT_PROPER_STATUS_FOR_THE_REQUEST(
            HttpStatus.BAD_REQUEST,
            "USER_400",
            "요청에 적합한 친구 요청 상태가 아닙니다."),
    FORBIDDEN_FRIEND_REMOVE_REQUEST(
            HttpStatus.FORBIDDEN,
            "USER_403",
            "해당 인증된 사용자가 삭제할 수 있는 친구가 아닙니다."),

    // BLOCKED USER
    CANT_BLOCK_SELF(
            HttpStatus.BAD_REQUEST,
            "USER_400",
            "스스로를 차단할 수 없습니다."),
    ALREADY_BLOCKED(
            HttpStatus.BAD_REQUEST,
            "USER_400",
            "이미 차단된 사용자입니다."),
    BLOCKED_USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER_404",
            "차단된 사용자를 찾을 수 없습니다."),
    REQUEST_FORBIDDEN_FOR_BLOCKED_USER(
            HttpStatus.FORBIDDEN,
            "USER_403",
            "차단된 사용자에 대해 할 수 없는 요청입니다."),
    FORBIDDEN_BLOCK_REQUEST(
            HttpStatus.FORBIDDEN,
            "USER_403",
            "해당 인증된 사용자가 취소할 수 있는 차단된 사용자가 아닙니다."),

    // POST
    POST_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "POST_404",
            "게시글을 찾을 수 없습니다."),
    POST_IMAGE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "POST_404",
            "게시글 이미지을 찾을 수 없습니다."),
    IMAGE_STATUS_ORDER_LIST_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "POST_400",
            "이미지 요청 정보 목록이 필요합니다."),
    IMAGE_STATUS_ORDER_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "POST_400",
            "이미지 요청 정보가 필요합니다."),
    CANT_UPLOAD_EMPTY_MULTIPART_FILE(
            HttpStatus.BAD_REQUEST,
            "POST_400",
            "null이거나 empty 상태의 파일을 업로드할 수 없습니다."),
    REQUESTED_IMG_LARGER_THAN_EMPTY_SLOT(
            HttpStatus.BAD_REQUEST,
            "POST_400",
            "빈 슬롯보다 요청 이미지가 많습니다."),
    DUPLICATE_IMAGE_ID(
            HttpStatus.BAD_REQUEST,
            "POST_400",
            "동일한 이미지를 중복 지정할 수 없습니다."),
    NOT_POST_AUTHOR(
            HttpStatus.FORBIDDEN,
            "POST_403",
            "해당 게시글의 작성자가 아닙니다."),
    MAXIMUM_COUNT_IS_THREE(
            HttpStatus.BAD_REQUEST,
            "POST_400",
            "이미지를 최대 3장까지 첨부할 수 있습니다."),

    // COMMENT
    COMMENT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "POST_404",
            "댓글을 찾을 수 없습니다."),
    NOT_COMMENT_AUTHOR(
            HttpStatus.FORBIDDEN,
            "POST_403",
            "해당 댓글의 작성자가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String customCode; // 도메인명 + 상태코드
    private final String message;
}
