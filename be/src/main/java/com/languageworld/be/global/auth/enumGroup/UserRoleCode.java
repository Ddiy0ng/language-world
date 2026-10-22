package com.languageworld.be.global.auth.enumGroup;

import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.RequiredArgsConstructor;

public enum UserRoleCode {

    ADMIN,
    USER,
    SERVICE_FORBIDDEN_USER;

    public static UserRoleCode from(String name) {

        UserRoleCode[] userRoleCodeArr = UserRoleCode.values();
        for(UserRoleCode userRoleCode : userRoleCodeArr) {

            if(userRoleCode.name().equals(name))
                return userRoleCode;
        }

        CustomLogger.warn(
                LogEventCode.AUTH,
                "FAIL",
                LogEventReasonCode.UNSUPPORTED_USER_ROLE_TYPE,
                LogEventReasonCode.UNSUPPORTED_USER_ROLE_TYPE.getMessage() + " - RequestType: " + name,
                null
        );

        throw new CustomException(CustomExceptionCode.UNSUPPORTED_USER_ROLE_TYPE);
    }
}// term 필수 동의안 하면 userRoleCode를 수정, 이건 다른 곳에도 쓰임
