package com.languageworld.be.global.config;

import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtExceptionHandleFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (CustomException customException) {

            response.setStatus(customException.getHttpStatus().value());

            ApiResponse<Void> apiResponse =
                    ApiResponse.<Void>builder()
                            .customCode(customException.getCustomCode())
                            .message(customException.getMessage())
                            .build();

            objectMapper.writeValue(
                    response.getWriter(),
                    apiResponse
            );
        }
    }
}
