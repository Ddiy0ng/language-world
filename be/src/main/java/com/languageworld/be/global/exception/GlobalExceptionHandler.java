package com.languageworld.be.global.exception;

import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import jakarta.validation.constraints.Null;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Null>> handleCustomException (CustomException customException) {

        return ResponseEntityUtil.fail(customException);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Null>> handleException (Exception exception) {

        log.error("undefined exception: {}", exception.getMessage(), exception); // 메시지만으로 원인 파악 불가할 수도 -> 스택 트레이스까지 출력
        return ResponseEntityUtil.fail(exception);
    }
}
