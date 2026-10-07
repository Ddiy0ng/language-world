package com.languageworld.be.global.jwt;

import com.languageworld.be.domain.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey JWT_SECRET_KEY;
    private final Long ACCESS_EXPIRATION;
    private final Long REFRESH_EXPIRATION;

    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration}") Long accessExpiration,
            @Value("${jwt.refresh-token-expiration}") Long refreshExpiration
    ) {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey); //base64로 인코딩된 문자열을 복호화
        JWT_SECRET_KEY = Keys.hmacShaKeyFor(keyBytes); // HS256 같은 HMAC 서명에 사용할 수 있는 SecretKey 객체로 변환
        ACCESS_EXPIRATION = accessExpiration;
        REFRESH_EXPIRATION = refreshExpiration;
    }

    // 액세스 토큰 생성
    public String generateAccessToken(User user) {

        Date now = new Date();

        String accessToken = Jwts.builder()
                .subject(user.getUuid().toString())
                .claim("userRole", user.getUserRole().name())
                .claim("tokenType", "ACCESS_TOKEN")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_EXPIRATION)) //만료 시각
                .signWith(JWT_SECRET_KEY)
                .compact();

        return accessToken;
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(User user) {

        Date now = new Date();

        String refreshToken = Jwts.builder()
                .subject(user.getUuid().toString())
                .claim("tokenType", "REFRESH_TOKEN")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + REFRESH_EXPIRATION)) //만료 시각
                .signWith(JWT_SECRET_KEY)
                .compact();

        return refreshToken;
    }
}
