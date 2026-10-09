package com.languageworld.be.global.config;

import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    // authentication error(인증)
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        CustomLogger.warn(
                LogEventCode.AUTH_FILTER,
                "FAIL",
                LogEventReasonCode.UNAUTHORIZED,
                LogEventReasonCode.UNAUTHORIZED.getMessage(),
                null
        );

        response.setStatus(CustomExceptionCode.UNAUTHORIZED.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ApiResponse<Void> apiResponse =
                ApiResponse.<Void>builder()
                        .customCode(CustomExceptionCode.UNAUTHORIZED.getCustomCode())
                        .message(CustomExceptionCode.UNAUTHORIZED.getMessage())
                        .build();

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }

}
