package com.languageworld.be.global.auth.enumGroup;

import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialTypeCode {

    GOOGLE("google"),
    KAKAO("kakao");

    private final String name;

    public static SocialTypeCode from(String name) {

        SocialTypeCode[] socialTypeCodeArr = SocialTypeCode.values();
        for(SocialTypeCode socialTypeCode : socialTypeCodeArr) {

            if(socialTypeCode.name.equals(name))
                return socialTypeCode;
        }

        CustomLogger.warn(
                LogEventCode.OAUTH2,
                "FAIL",
                LogEventReasonCode.UNSUPPORTED_SOCIAL_TYPE,
                LogEventReasonCode.UNSUPPORTED_SOCIAL_TYPE.getMessage() + " - RequestType: " + name,
                null
        );

        throw new CustomException(CustomExceptionCode.UNSUPPORTED_SOCIAL_TYPE);
    }
}