package com.languageworld.be.global.auth.service;

import com.languageworld.be.domain.term.repository.AgreedTermRepository;
import com.languageworld.be.domain.term.service.TermUtil;
import com.languageworld.be.domain.user.entity.SocialAccount;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.repository.SocialAccountRepository;
import com.languageworld.be.global.auth.entity.SocialAuthorizedUser;
import com.languageworld.be.global.auth.enumGroup.SocialTypeCode;
import com.languageworld.be.global.auth.enumGroup.UserRoleCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final GoogleUtil googleUtil;
    private final KakaoUtil kakaoUtil;
    private final TermUtil termUtil;
    private final SocialAccountRepository socialAccountRepository;
    private final AgreedTermRepository agreedTermRepository;

    // 소셜 코드에 따라 인증 유저 가져오기
    protected SocialAuthorizedUser getSocialAuthorizedUserBySocialType(SocialTypeCode socialTypeCode, String token) {

        return switch(socialTypeCode) {
            case GOOGLE -> googleUtil.verifyGoogleIdToken(token);
            case KAKAO -> kakaoUtil.getSocialAuthorizedUser(token);
        };
    }

    // 사용자 존재 여부 확인: signup type + sub
    protected void validateUserExistForSignup(SocialTypeCode socialTypeCode, String sub) {

        Optional<SocialAccount> optionalSocialAccount = socialAccountRepository.findBySignupTypeAndSub(socialTypeCode, sub);
        if(optionalSocialAccount.isPresent()) {

            CustomLogger.warn(
                    LogEventCode.SOCIAL,
                    "FAIL",
                    LogEventReasonCode.SOCIAL_SUB_ALREADY_EXIST,
                    LogEventReasonCode.SOCIAL_SUB_ALREADY_EXIST.getMessage() + " - type: " + socialTypeCode + ", sub: " + sub,
                    null
            );

            throw new CustomException(CustomExceptionCode.ACCOUNT_ALREADY_EXIST);
        }
    }

    // 사용자 존재 여부 확인: login type + sub
    protected SocialAccount validateUserExistForLogin(SocialTypeCode loginTypeCode, String sub) {

        Optional<SocialAccount> optionalSocialAccount = socialAccountRepository.findBySignupTypeAndSub(loginTypeCode, sub);
        if(optionalSocialAccount.isEmpty()) {

            CustomLogger.warn(
                    LogEventCode.SOCIAL,
                    "FAIL",
                    LogEventReasonCode.SOCIAL_SUB_NOT_EXIST,
                    LogEventReasonCode.SOCIAL_SUB_NOT_EXIST.getMessage() + " - type: " + loginTypeCode +  " - sub: " + sub,
                    null
            );

            throw new CustomException(CustomExceptionCode.ACCOUNT_NOT_EXIST);
        }

        SocialAccount socialAccount = optionalSocialAccount.get();

        return socialAccount;
    }

    // 약관 동의 상태에 따라 권한 변경
    protected void userRoleChangeAccordingToTerm(User user) {

        UserRoleCode userTypeCode = termUtil.validateTermCheck(agreedTermRepository.findAllByUser_Uuid(user.getUuid()));

        user.changeUserRole(userTypeCode);
    }

    // 연령대 계산
    protected int calculateAgeGroup(LocalDate birth) {

        LocalDate now = LocalDate.now();

        int age = Period.between(birth, LocalDate.now()).getYears();
        int ageGroup = (age / 10) * 10;

        return ageGroup;
    }
}
