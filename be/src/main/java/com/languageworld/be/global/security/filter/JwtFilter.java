package com.languageworld.be.global.security.filter;

import com.languageworld.be.global.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // jwt 추출
        String token = jwtUtil.resolveToken(request);

        if (token == null) {
            filterChain.doFilter(request, response); // null인 경우 다음 필터로 넘김

            return; // null인 경우 더 진행되지 않도록 return
        }

        Claims claims = jwtUtil.getValidateClaimsFromToken(JwtUtil.ACCESS, token);

        // 인증 객체 생성
        Authentication authentication = jwtUtil.createAuthentication(claims);

        // Spring Context에 인증 정보 저장
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response); // 다음 필터로 넘김
    }
}
