package com.languageworld.be.global.auth.service;

import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.Period;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 비밀번호 유효성 검사(공백 + 형식)
    protected void validatePassword(String password) {

        // 공백 검사
        if(password == null || password.isBlank()) {
            CustomLogger.warn(
                    LogEventCode.SIGNUP,
                    "FAIL",
                    LogEventReasonCode.PASSWORD_REQUIRED,
                    LogEventReasonCode.PASSWORD_MISMATCH.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.PASSWORD_REQUIRED);
        }

        // 형식 검사
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=])[A-Za-z\\d!@#$%^&*()_+\\-=]{8,20}$";
        if(!password.matches(regex)) {
            CustomLogger.warn(
                    LogEventCode.SIGNUP,
                    "FAIL",
                    LogEventReasonCode.INVALID_PASSWORD,
                    LogEventReasonCode.INVALID_PASSWORD.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.INVALID_PASSWORD);
        }
    }

    // 비밀번호 암호화
    protected String encodePassword(String rawPassword) {

        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);

        return encodedPassword;
    }

    // 비밀번호 일치 여부
    protected boolean isPasswordMatches(String rawPassword, String encodedPassword) {

        if(!bCryptPasswordEncoder.matches(rawPassword, encodedPassword)) {
            CustomLogger.warn(
                    LogEventCode.LOGIN,
                    "FAIL",
                    LogEventReasonCode.PASSWORD_MISMATCH,
                    LogEventReasonCode.PASSWORD_MISMATCH.getMessage(),
                    null
            );

            return false;
        }

        return true;
    }


    // 나이대 계산
    protected int calculateAgeGroup(LocalDate birth) {

        LocalDate now = LocalDate.now();

        int age = Period.between(birth, LocalDate.now()).getYears();
        int ageGroup = (age / 10) * 10;

        return ageGroup;
    }

}
