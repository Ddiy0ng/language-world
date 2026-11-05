package com.languageworld.be.global.jwt;

import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    public final static String JWT_PREFIX = "Bearer ";
    public final static String AUTHORIZATION = "Authorization";
    public final static String ROLE = "userRole";
    public final static String TYPE = "tokenType";
    public final static String ACCESS = "ACCESS_TOKEN";
    public final static String REFRESH = "REFRESH_TOKEN";

    private final SecretKey secretKey;

    // jwt 추출
    public String resolveToken(HttpServletRequest httpServletRequest) {

        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);

        // auth header 검증
        if(authorizationHeader == null)
            return null;

        // "Bearer 확인 및 제거
        if(!authorizationHeader.startsWith(JWT_PREFIX))
            return null;

        return authorizationHeader.substring(JWT_PREFIX.length());
    }

    // Claim 추출
    private Claims getClaims(String token) {

        Claims claims;

        try {
            claims = Jwts.parser()
                    .verifyWith(secretKey)
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

    public Claims getValidateClaimsFromToken(String purpose, String token) {

        if(purpose.equals(REFRESH)) {

            if (token == null) {

                CustomLogger.warn(
                        LogEventCode.JWT,
                        "FAIL",
                        LogEventReasonCode.TOKEN_IS_NULL,
                        LogEventReasonCode.TOKEN_IS_NULL.getMessage(),
                        null
                );

                throw new CustomException(CustomExceptionCode.INVALID_TOKEN); //CustomException 처리: 토큰 공백
            }
        }

        if (token.isBlank()) {

            CustomLogger.warn(
                    LogEventCode.JWT,
                    "FAIL",
                    LogEventReasonCode.TOKEN_IS_BLANK,
                    LogEventReasonCode.TOKEN_IS_BLANK.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.INVALID_TOKEN); //CustomException 처리: 토큰 공백
        }

        Claims claims = getClaims(token);

        switch(purpose) {
            case ACCESS -> {

                if(!ACCESS.equals(getTokenType(claims)) || claims.getSubject() == null || claims.getSubject().isBlank()) {

                    CustomLogger.warn(
                            LogEventCode.JWT,
                            "FAIL",
                            LogEventReasonCode.ACCESS_TOKEN_REQUIRED,
                            LogEventReasonCode.ACCESS_TOKEN_REQUIRED.getMessage() + " - Requested token type: " + getTokenType(claims),
                            null
                    );

                    throw new CustomException(CustomExceptionCode.INVALID_TOKEN); // CustomException 처리: 액세스 토큰 필요
                }
            }
            case REFRESH -> {

                if(!REFRESH.equals(getTokenType(claims)) || claims.getId() == null || claims.getId().isBlank() || claims.getSubject() == null || claims.getSubject().isBlank()) {

                    CustomLogger.warn(
                            LogEventCode.JWT,
                            "FAIL",
                            LogEventReasonCode.ACCESS_TOKEN_REQUIRED,
                            LogEventReasonCode.ACCESS_TOKEN_REQUIRED.getMessage() + " - Requested token type: " + getTokenType(claims),
                            null
                    );

                    throw new CustomException(CustomExceptionCode.INVALID_TOKEN); // CustomException 처리: 액세스 토큰 필요
                }
            }
        }

        return claims;
    }

    // userUuid 추출
    public String getUuid(Claims claims) {

        return claims.getSubject();
    }

    // userRole 추출
    public String getUserRole(Claims claims) {

        return claims.get(ROLE, String.class);
    }

    // tokenType 추출
    public String getTokenType(Claims claims) {

        return claims.get(TYPE, String.class);
    }

    // Authentication 생성
    public Authentication createAuthentication(Claims claims) {

        String uuid = getUuid(claims);
        String userRole = getUserRole(claims);

        CustomPrincipal customPrincipal = CustomPrincipal.of(uuid, userRole);

        return UsernamePasswordAuthenticationToken
                .authenticated(
                        customPrincipal,
                        null,
                        List.of(new SimpleGrantedAuthority(getUserRole(claims)))
                );
    }

}
