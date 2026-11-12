package com.languageworld.be.global.webSocket;

import com.languageworld.be.global.auth.entity.CustomPrincipal;
import lombok.Builder;

import java.util.Date;
import java.util.UUID;

@Builder
public record CustomWebSocketSession(

        CustomPrincipal customPrincipal,
        Date expiration
) {

    public static  CustomWebSocketSession of(CustomPrincipal customPrincipal, Date expiration) {

        return CustomWebSocketSession.builder()
                .customPrincipal(customPrincipal).expiration(expiration)
                .build();
    }
}
