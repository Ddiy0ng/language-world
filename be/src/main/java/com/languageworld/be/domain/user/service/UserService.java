package com.languageworld.be.domain.user.service;

import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.language.service.LanguageUtil;
import com.languageworld.be.domain.user.dto.request.*;
import com.languageworld.be.domain.user.dto.response.UserInfoResponseDto;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import com.languageworld.be.domain.user.entity.ServiceAccount;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.repository.UserRepository;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.auth.service.PasswordUtil;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserUtil userUtil;
    private final LanguageUtil languageUtil;
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

    @Transactional
    public UserInfoResponseDto findUserInfo(UUID uuid) {

        User user = userUtil.getUserByUuid(uuid);

        UserInfoResponseDto userInfoResponseDto = UserInfoResponseDto.from(user);

        return userInfoResponseDto;
    }

    @Transactional
    public List<UserSummaryResponseDto> searchUserSummaryList(CustomPrincipal customPrincipal, String keyword, String learningLanguage, String level) {

        // 스스로는 조회 제외
        UUID authenticatedUserUuid = customPrincipal.uuid();
        List<User> userList = userRepository.findAllByUuidNot(authenticatedUserUuid);

        List<UserSummaryResponseDto> userSummaryResponseDtoList = userList.stream()
                .map(UserSummaryResponseDto::from)
                .toList();

        return userSummaryResponseDtoList;
    }

    @Transactional
    public void changeUserInfo(CustomPrincipal customPrincipal, ChangeUserInfoRequestDto changeUserInfoRequestDto) {

        User user = userUtil.getUserByUuid(customPrincipal.uuid());

        if(changeUserInfoRequestDto.name() != null)
            user.changeUserName(changeUserInfoRequestDto.name());
        if(changeUserInfoRequestDto.bio() != null)
            user.changeUserBio(changeUserInfoRequestDto.bio());
        if(changeUserInfoRequestDto.nation() != null)
            user.changeUserNation(changeUserInfoRequestDto.nation());
        if(changeUserInfoRequestDto.motherTongueId() != null) {
            Language motherTongue = languageUtil.getLanguageById(changeUserInfoRequestDto.motherTongueId());
            user.changeUserMotherTongue(motherTongue);
        }
    }

    @Transactional
    public void changeUserLanguageInfo(CustomPrincipal customPrincipal, ChangeUserLanguageInfoRequestDto changeUserLanguageInfoRequestDto) {

        User user = userUtil.getUserByUuid(customPrincipal.uuid());

        if((changeUserLanguageInfoRequestDto.learningLanguageId() == null) != (changeUserLanguageInfoRequestDto.learningLanguageLevelId() == null)) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.LEARNING_LANGUAGE_AND_LEVEL_BOTH_REQUIRE,
                    LogEventReasonCode.LEARNING_LANGUAGE_AND_LEVEL_BOTH_REQUIRE.getMessage()
                            + " - learningLanguageId: " + changeUserLanguageInfoRequestDto.learningLanguageId()
                            + " - levelId: " + changeUserLanguageInfoRequestDto.learningLanguageLevelId(),
                    null
            );

            throw new CustomException(CustomExceptionCode.LEARNING_LANGUAGE_AND_LEVEL_BOTH_REQUIRE);
        }

        Language learningLanguage = languageUtil.getLanguageById(changeUserLanguageInfoRequestDto.learningLanguageId());
        Level level = languageUtil.getLevelById(changeUserLanguageInfoRequestDto.learningLanguageLevelId());

        user.changeUserLearningLanguageAndLevel(learningLanguage, level);
    }

    @Transactional
    public void changeUserPassword(CustomPrincipal customPrincipal, ChangeUserPasswordRequestDto changeUserPasswordRequestDto) {

        ServiceAccount serviceAccount = userUtil.getServiceAccountByUserUuid(customPrincipal.uuid());

        String encodedOldPassword = serviceAccount.getPassword();

        // 일치 검사
        passwordUtil.isPasswordMatches(encodedOldPassword, changeUserPasswordRequestDto.oldPassword());

        // 요청 비밀번호 형식 검사
        passwordUtil.validatePassword(changeUserPasswordRequestDto.newPassword());

        // 암호화
        String newPassword = passwordUtil.encodePassword(changeUserPasswordRequestDto.newPassword());

        serviceAccount.changeUserPassword(newPassword);
    }

    @Transactional
    public void changeUserProfile(CustomPrincipal customPrincipal, ChangeUserProfileRequestDto changeUserProfileRequestDto) {

        User user = userUtil.getUserByUuid(customPrincipal.uuid());

        if(changeUserProfileRequestDto.profileImageUrl() != null)
            user.changeUserProfileImageUrl(changeUserProfileRequestDto.profileImageUrl());
    }

    @Transactional
    public void removeUser(CustomPrincipal customPrincipal) {

        // 서비스 유저면 서비스 계정 삭제

        // 소셜 유저면 소셜 계정 삭제

        // 사용자 테이블의 식별 컬럼 null 처리

        // deleted 처리로 softDelete
    }
}
