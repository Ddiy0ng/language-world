package com.languageworld.be.global.response;

import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.enumGroup.SuccessCode;
import org.springframework.http.ResponseEntity;

public class ResponseEntityUtil {

    // 성공 + 데이터 o
    public static <T> ResponseEntity<ApiResponse<T>> success (SuccessCode successCode, T data) {

        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .customCode(successCode.getCustomCode())
                .data(data)
                .message(successCode.getMessage())
                .build();

        return ResponseEntity.status(successCode.getHttpStatus()).body(apiResponse);
    }

    // 성공 + 데이터 x
    public static ResponseEntity<ApiResponse<Void>> success (SuccessCode successCode) {

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .customCode(successCode.getCustomCode())
                .message(successCode.getMessage())
                .build();

        return ResponseEntity.status(successCode.getHttpStatus()).body(apiResponse);
    }

    // 실패(예외처리용)
    public static ResponseEntity<ApiResponse<Void>> fail (CustomException customException) {

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .customCode(customException.getCustomCode())
                .message(customException.getMessage())
                .build();

        return ResponseEntity.status(customException.getHttpStatus()).body(apiResponse);
    }

    // 미정의 실패
    public static ResponseEntity<ApiResponse<Void>> fail () {

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .customCode(CustomExceptionCode.INTERNAL_SERVER_ERROR.getCustomCode())
                .message(CustomExceptionCode.INTERNAL_SERVER_ERROR.getMessage())
                .build();

        return ResponseEntity.status(CustomExceptionCode.INTERNAL_SERVER_ERROR.getHttpStatus()).body(apiResponse);
    }
}
