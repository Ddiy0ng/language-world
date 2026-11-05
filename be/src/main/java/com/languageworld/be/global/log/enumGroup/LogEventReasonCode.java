package com.languageworld.be.global.log.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LogEventReasonCode {

    // AUTH
    UNAUTHORIZED("인증되지 않은 사용자입니다."),
    UNSUPPORTED_USER_ROLE_TYPE("지원하지 않는 권한입니다."),
    ADMIN_ACCESS_REQUIRED("관리자 권한이 필요합니다."),
    DELETED_USER("탈퇴한 사용자입니다."),
    EMAIL_ALREADY_EXIST("이미 가입된 이메일입니다."),
    EMAIL_NOT_FOUND("존재하지 않는 이메일입니다."),
    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다."),
    PASSWORD_REQUIRED("비밀번호를 입력해주세요."),
    INVALID_PASSWORD("영문, 숫자, 특수문자의 8~20자 이내의 비밀번호여야 합니다"),

    // JWT
    EXPIRED_TOKEN("만료된 토큰입니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    TOKEN_IS_NULL("토큰이 null입니다."),
    TOKEN_IS_BLANK("토큰이 Blank입니다."),
    ACCESS_TOKEN_REQUIRED("액세스 토큰이 필요합니다."),
    TOKEN_SUB_DB_UUID_NOT_SAME("토큰의 sub와 db에 저장된 토큰 소유자의 uuid가 일치하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND("리프레시 토큰을 찾을 수 없습니다."),
    NOT_TOKEN_OWNER("토큰의 소유주가 아닙니다."),
    DELETED_TOKEN("삭제 처리된 토큰입니다."),

    // SOCIAL
    INVALID_SOCIAL_TOKEN("유효하지 않은 소셜 인증 토큰입니다."),
    SOCIAL_API_REQUEST_FAILED("소셜 인증 서버와 통신하는 중 문제가 발생했습니다."),
    SOCIAL_SUB_ALREADY_EXIST("이미 소셜 회원가입이 완료된 사용자입니다."),
    SOCIAL_SUB_NOT_EXIST("존재하지 않는 사용자입니다."),
    UNSUPPORTED_SOCIAL_TYPE("지원하지 않는 소셜 타입입니다."),
    INVALID_GOOGLE_TOKEN("유효하지 않은 구글 토큰입니다."),
    SOCIAL_TOKEN_VERIFICATION_FAILED("소셜 인증 토큰 검증 중 문제가 발생했습니다."),
    INVALID_KAKAO_RESPONSE("유효하지 않은 카카오 응답입니다."),

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
    MULTIPART_FILE_DELETE_FAILED("파일 삭제에 실패했습니다."),
    FILE_TRANSACTION_STATUS_UNKNOWN("트랜잭션  커밋 또는 롤백 여부를 확인할 수 없습니다."),

    // TERM
    TERM_NOT_FOUND("약관을 찾을 수 없습니다."),
    AGREED_TERM_NOT_FOUND("동의한 약관을 찾을 수 업습니다."),
    TERM_ALREADY_EXIST("이미 등록된 약관입니다."),
    NOT_AGREED_TERM_OWNER("본인이 동의한 약관이 아닙니다."),
    REQUIRED_TERM_NOT_AGREED("활성화되지 않은 약관입니다."),
    UNMATCHED_TERM_PURPOSE("약관 목적이 요청받은 약관의 목적과 일치하지 않습니다."),

    // LANGUAGE
    UNSUPPORTED_LANGUAGE("지원하지 않는 언어입니다."),
    LEVEL_NOT_FOUND("해당 등급이 존재하지 않습니다."),
    NOT_PROPER_LEVEL_FOR_LANGUAGE("해당 언어에서 제공하는 등급이 아닙니다."),

    // USER
    USER_NOT_FOUND("존재하지 않는 사용자입니다."),
    LEARNING_LANGUAGE_AND_LEVEL_BOTH_REQUIRE("학습어와 레벨 모두 필요합니다."),
    PROVIDED_FOR_SERVICE_USER("서비스 로그인 사용자에게만 제공되는 기능입니다."),

    // FRIEND
    CANT_ADD_SELF_AS_FRIEND("스스로를 친구추가할 수 없습니다."),
    ALREADY_FRIEND("이미 친구추가된 사용자입니다."),
    RECEIVED_FRIEND_REQUEST_NOT_FOUND("받은 친구 요청이 없습니다."),
    SENT_FRIEND_REQUEST_NOT_FOUND("전송한 친구 요청이 없습니다."),
    ACCEPTED_FRIEND_REQUEST_NOT_FOUND("수락한 친구 요청이 없습니다."),
    NOT_PROPER_STATUS_FOR_THE_REQUEST("요청에 적합한 친구 요청 상태가 아닙니다."),
    CANT_CANCEL_ACCEPTED_REQUEST("친구 요청을 수락한 상태일 때만 취소가 가능합니다."),
    FORBIDDEN_FRIEND_REMOVE_REQUEST("해당 인증된 사용자가 삭제할 수 있는 친구가 아닙니다."),
    // BLOCKED USER
    CANT_BLOCK_SELF("스스로를 차단할 수 없습니다."),
    ALREADY_BLOCKED("이미 차단된 사용자입니다."),
    BLOCKED_USER_NOT_FOUND("차단된 사용자를 찾을 수 없습니다."),
    REQUEST_FORBIDDEN_FOR_BLOCKED_USER("차단된 사용자에 대해 할 수 없는 요청입니다."),
    FORBIDDEN_BLOCK_REQUEST("해당 인증된 사용자가 취소할 수 있는 차단된 사용자가 아닙니다."),
    IMAGE_STATUS_ORDER_LIST_REQUIRED("이미지 요청 정보 목록이 필요합니다."),
    IMAGE_STATUS_ORDER_REQUIRED("이미지 요청 정보가 필요합니다."),
    CANT_UPLOAD_EMPTY_MULTIPART_FILE("null이거나 empty 상태의 파일을 업로드할 수 없습니다."),
    REQUESTED_IMG_LARGER_THAN_EMPTY_SLOT("빈 슬롯보다 요청 이미지가 많습니다."),
    DUPLICATE_IMAGE_ID("동일한 이미지를 중복 지정할 수 없습니다."),

    // POST
    POST_NOT_FOUND("게시글을 찾을 수 없습니다."),
    POST_IMAGE_NOT_FOUND("게시글 이미지을 찾을 수 없습니다."),
    NOT_POST_AUTHOR("해당 게시글의 작성자가 아닙니다."),
    MAXIMUM_COUNT_IS_THREE("이미지를 최대 3장까지 첨부할 수 있습니다."),

    // COMMENT
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다."),
    NOT_COMMENT_AUTHOR("해당 댓글의 작성자가 아닙니다."),

    // LIKE
    ALREADY_LIKED_POST("이미 좋아요 된 게시글입니다."),
    LIKE_NOT_FOUND("좋아요를 찾을 수 없습니다."),
    NOT_LIKE_AUTHOR("해당 좋아요를 추가한 사용자가 아닙니다.");


    private final String message;
}