package com.languageworld.be.global.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApiResponse<T> {

    private final String customCode;
    private T data;
    private final String message;

}
