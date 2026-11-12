package com.languageworld.be.global.jwt;

import com.languageworld.be.domain.user.entity.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private final SecretKey JWT_SECRET_KEY;
    private final Long ACCESS_EXPIRATION;
    private final Long REFRESH_EXPIRATION;

    public JwtProvider(
            SecretKey secretKey,
            @Value("${jwt.access-token-expiration}") Long accessExpiration,
            @Value("${jwt.refresh-token-expiration}") Long refreshExpiration
    ) {

        JWT_SECRET_KEY = secretKey;
        ACCESS_EXPIRATION = accessExpiration;
        REFRESH_EXPIRATION = refreshExpiration;
    }

    // 액세스 토큰 생성
    public String generateAccessToken(User user) {

        Date now = new Date();

        return Jwts.builder()
                .subject(user.getUuid().toString())
                .claim(JwtUtil.ROLE, user.getUserRole().name())
                .claim(JwtUtil.TYPE, JwtUtil.ACCESS)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_EXPIRATION)) //만료 시각
                .signWith(JWT_SECRET_KEY)
                .compact();
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(User user) {

        Date now = new Date();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUuid().toString())
                .claim(JwtUtil.TYPE, JwtUtil.REFRESH)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + REFRESH_EXPIRATION)) //만료 시각
                .signWith(JWT_SECRET_KEY)
                .compact();
    }
}
