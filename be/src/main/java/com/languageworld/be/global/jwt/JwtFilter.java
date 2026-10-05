package com.languageworld.be.global.jwt;

import com.languageworld.be.global.auth.entity.CustomPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

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

        if (token.isBlank())
            throw new RuntimeException(); //CustomException 처리: 토큰 공백

        // claim 추출
        Claims claims = jwtUtil.getClaims(token);

        // tokenType 검출
        if("ACCESS_TOKEN".equals(jwtUtil.getTokenType(claims)))
            throw new RuntimeException(); // CustomException 처리: 액세스 토큰 필요

        // 인증 사용자 객체 셍성
        CustomPrincipal customPrincipal = new CustomPrincipal(jwtUtil.getUsername(claims), jwtUtil.getUserRole(claims));

        // 인증 객체 생성
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                customPrincipal,
                null,
                List.of(new SimpleGrantedAuthority(jwtUtil.getUserRole(claims)))
        );

        // Spring Context에 인증 정보 저장
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response); // 다음 필터로 넘김
    }
}
