package com.languageworld.be.global.jwt;

import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.enumGroup.LogEventCode;
import com.languageworld.be.global.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final static String JWT_PREFIX = "Bearer ";
    private final SecretKey JWT_SECRET_KEY;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey); //base64로 인코딩된 문자열을 복호화
        JWT_SECRET_KEY = Keys.hmacShaKeyFor(keyBytes); // HS256 같은 HMAC 서명에 사용할 수 있는 SecretKey 객체로 변환
    }

    // jwt 추출
    protected String resolveToken(HttpServletRequest httpServletRequest) {

        String authorizationHeader = httpServletRequest.getHeader("Authorization");

        // auth header 검증
        if(authorizationHeader == null)
            return null;

        // "Bearer 확인 및 제거
        if(!authorizationHeader.startsWith(JWT_PREFIX))
            return null;

        String token = authorizationHeader.substring(JWT_PREFIX.length());

        return token;
    }

    // Claim 추출
    protected Claims getClaims(String token) {

        Claims claims;

        try {
            claims = Jwts.parser()
                    .verifyWith(JWT_SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException expiredJwtException) {

            CustomLogger.warn(
                    LogEventCode.JWT,
                    "FAIL",
                    LogEventReasonCode.EXPIRED_TOKEN,
                    LogEventReasonCode.EXPIRED_TOKEN.getMessage(),
                    null
            );
            throw new CustomException(CustomExceptionCode.EXPIRED_TOKEN); // CustomException 처리: 만료된 토큰 처리
        } catch (JwtException | IllegalArgumentException e) {

            CustomLogger.warn(
                    LogEventCode.JWT,
                    "FAIL",
                    LogEventReasonCode.INVALID_TOKEN,
                    LogEventReasonCode.INVALID_TOKEN.getMessage() + "JwtException | IllegalArgumentException while parsing jwt to get claims",
                    null
            );
            throw new CustomException(CustomExceptionCode.INVALID_TOKEN); // CustomException 처리: 유효하지 않은 토큰 처리
        }

        return claims;
    }

    // username 추출
    protected String getUsername(Claims claims) {

        String username = claims.getSubject();

        return username;
    }

    // userRole 추출
    protected String getUserRole(Claims claims) {

        String userRole = claims.get("userRole", String.class);

        return userRole;
    }

    // tokenType 추출
    protected String getTokenType(Claims claims) {

        String tokenType = claims.get("tokenType", String.class);

        return tokenType;
    }
}
