package com.languageworld.be.domain.user.service;

import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.language.service.LanguageUtil;
import com.languageworld.be.domain.user.dto.request.ChangeUserInfoRequestDto;
import com.languageworld.be.domain.user.dto.request.ChangeUserLanguageInfoRequestDto;
import com.languageworld.be.domain.user.dto.request.ChangeUserPasswordRequestDto;
import com.languageworld.be.domain.user.dto.response.UserInfoResponseDto;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import com.languageworld.be.domain.user.entity.ServiceAccount;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.repository.UserRepository;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.auth.service.PasswordUtil;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.multiPartFile.FileUtil;
import com.languageworld.be.global.multiPartFile.enumGroup.FilePurposeCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserUtil userUtil;
    private final LanguageUtil languageUtil;
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final FileUtil fileUtil;

    @Transactional
    public UserInfoResponseDto findUserInfo(UUID uuid) {

        User user = userUtil.getUserByUuid(uuid);

        return UserInfoResponseDto.from(user);
    }

    @Transactional
    public List<UserSummaryResponseDto> searchUserSummaryList(CustomPrincipal customPrincipal, String keyword, Long learningLanguageId, Long learningLanguageLevelId) {

        // 스스로는 조회 제외
        UUID authenticatedUserUuid = customPrincipal.uuid();
        List<User> userList = userRepository.findAllByUuidNot(authenticatedUserUuid, keyword, learningLanguageId, learningLanguageLevelId);

        return userList.stream()
                .map(UserSummaryResponseDto::from)
                .toList();
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
    public void changeUserProfile(CustomPrincipal customPrincipal, MultipartFile multipartFile) {

        User user = userUtil.getUserByUuid(customPrincipal.uuid());

        List<String> commitDeleteFilePathList = new ArrayList<>();
        List<String> rollbackDeleteFilePathList = new ArrayList<>();
        fileUtil.registerCleanUp(FilePurposeCode.POST, commitDeleteFilePathList, rollbackDeleteFilePathList);

        String oldProfileImageUrl = user.getProfileImageUrl();
        commitDeleteFilePathList.add(oldProfileImageUrl);

        String newProfileImageUrl = fileUtil.uploadFile(multipartFile, FilePurposeCode.PROFILE);
        rollbackDeleteFilePathList.add(newProfileImageUrl);
        user.changeUserProfileImageUrl(newProfileImageUrl);

        fileUtil.deleteFile(FilePurposeCode.PROFILE, oldProfileImageUrl);
    }
}
