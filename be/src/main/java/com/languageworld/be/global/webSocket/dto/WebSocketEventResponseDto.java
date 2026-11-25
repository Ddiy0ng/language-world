package com.languageworld.be.global.webSocket.dto;

import com.languageworld.be.global.webSocket.enumGroup.WebSocketEventTypeCode;
import lombok.Builder;

@Builder
public record WebSocketEventResponseDto<T> (

        WebSocketEventTypeCode type,
        T data
){
    public static <T> WebSocketEventResponseDto<T> from(WebSocketEventTypeCode webSocketEventTypeCode, T data) {

        return WebSocketEventResponseDto.<T>builder()
                .type(webSocketEventTypeCode).data(data)
                .build();
    }
}
