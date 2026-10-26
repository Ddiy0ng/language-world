package com.languageworld.be.domain.user.service;

import com.languageworld.be.domain.user.entity.ServiceAccount;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.repository.ServiceAccountRepository;
import com.languageworld.be.domain.user.repository.UserRepository;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserUtil {

    private final UserRepository userRepository;
    private final ServiceAccountRepository serviceAccountRepository;

    public User getUserByUuid(UUID uuid) {

        Optional<User> optionalUser = userRepository.findByUuid(uuid);
        if(optionalUser.isEmpty()) {
            CustomLogger.warn(LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.USER_NOT_FOUND,
                    LogEventReasonCode.USER_NOT_FOUND.getMessage() + " - UserUuid: " + uuid,
                    null
            );

            throw new CustomException(CustomExceptionCode.USER_NOT_FOUND);
        }

        return optionalUser.get();
    }

    protected ServiceAccount getServiceAccountByUserUuid(UUID userUuid) {

        Optional<ServiceAccount> optionalServiceAccount = serviceAccountRepository.findByUser_Uuid(userUuid);

        if(optionalServiceAccount.isEmpty()) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.PROVIDED_FOR_SERVICE_USER,
                    LogEventReasonCode.PROVIDED_FOR_SERVICE_USER.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.NOT_SUPPORTED__FOR_SOCIAL_USER);
        }

        ServiceAccount serviceAccount = optionalServiceAccount.get();

        return serviceAccount;
    }

    // 나이대 계산
    private int calculateAgeGroup(LocalDate birth) {

        LocalDate now = LocalDate.now();

        int age = Period.between(birth, LocalDate.now()).getYears();
        int ageGroup = (age / 10) * 10;

        return ageGroup;
    }
}
