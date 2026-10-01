package com.languageworld.be.global.response;

import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.enumGroup.SuccessCode;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
@RequiredArgsConstructor
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
    public static <Null> ResponseEntity<ApiResponse<Null>> success (SuccessCode successCode) {

        ApiResponse<Null> apiResponse = ApiResponse.<Null>builder()
                .customCode(successCode.getCustomCode())
                .message(successCode.getMessage())
                .build();

        return ResponseEntity.status(successCode.getHttpStatus()).body(apiResponse);
    }

    // 실패(예외처리용)
    public static <Null> ResponseEntity<ApiResponse<Null>> fail (CustomException customException) {

        ApiResponse<Null> apiResponse = ApiResponse.<Null>builder()
                .customCode(customException.getCustomCode())
                .message(customException.getMessage())
                .build();

        return ResponseEntity.status(customException.getHttpStatus()).body(apiResponse);
    }

    // 미정의 실패
    public static <Null> ResponseEntity<ApiResponse<Null>> fail (Exception e) {

        ApiResponse<Null> apiResponse = ApiResponse.<Null>builder()
                .customCode("UNDEFINED_EXCEPTION_500")
                .message("500에러:정의되지 않은 오류가 발생했습니다.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
