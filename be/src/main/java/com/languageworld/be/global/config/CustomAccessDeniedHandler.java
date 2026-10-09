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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    // authorization error(인가)
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String uri = request.getRequestURI();

        if(uri.equals("/admin") || uri.startsWith("/admin/")) {

            CustomLogger.warn(
                    LogEventCode.AUTH_FILTER,
                    "FAIL",
                    LogEventReasonCode.ADMIN_ACCESS_REQUIRED,
                    LogEventReasonCode.ADMIN_ACCESS_REQUIRED.getMessage(),
                    null
            );

            response.setStatus(CustomExceptionCode.ADMIN_ACCESS_DENIED.getHttpStatus().value());

            ApiResponse<Void> apiResponse =
                    ApiResponse.<Void>builder()
                            .customCode(CustomExceptionCode.ADMIN_ACCESS_DENIED.getCustomCode())
                            .message(CustomExceptionCode.ADMIN_ACCESS_DENIED.getMessage())
                            .build();

            objectMapper.writeValue(response.getWriter(), apiResponse);
        }
        else {

            CustomLogger.warn(
                    LogEventCode.AUTH_FILTER,
                    "FAIL",
                    LogEventReasonCode.ACCESS_REQUIRED,
                    LogEventReasonCode.ACCESS_REQUIRED.getMessage(),
                    null
            );

            response.setStatus(CustomExceptionCode.ACCESS_DENIED.getHttpStatus().value());
            ApiResponse<Void> apiResponse =
                    ApiResponse.<Void>builder()
                            .customCode(CustomExceptionCode.ACCESS_DENIED.getCustomCode())
                            .message(CustomExceptionCode.ACCESS_DENIED.getMessage())
                            .build();

            objectMapper.writeValue(response.getWriter(), apiResponse);
        }
    }
}
