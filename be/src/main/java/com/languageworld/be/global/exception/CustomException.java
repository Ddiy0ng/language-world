package com.languageworld.be.global.exception;

import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final String customCode;

    public CustomException(CustomExceptionCode customExceptionCode) {

        super(customExceptionCode.getMessage());
        httpStatus = customExceptionCode.getHttpStatus();
        customCode = customExceptionCode.getCustomCode();
    }
}
