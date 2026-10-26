package com.languageworld.be.domain.user.service;

import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.language.service.LanguageUtil;
import com.languageworld.be.domain.term.entity.AgreedTerm;
import com.languageworld.be.domain.term.repository.AgreedTermRepository;
import com.languageworld.be.domain.term.service.TermService;
import com.languageworld.be.domain.term.service.TermUtil;
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
import com.languageworld.be.global.multiPartFileUtil.FileUtil;
import com.languageworld.be.global.multiPartFileUtil.enumGroup.FilePurposeCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final TermUtil termUtil;

    @Transactional
    public UserInfoResponseDto findUserInfo(UUID uuid) {

        User user = userUtil.getUserByUuid(uuid);

        UserInfoResponseDto userInfoResponseDto = UserInfoResponseDto.from(user);

        return userInfoResponseDto;
    }

    @Transactional
    public List<UserSummaryResponseDto> searchUserSummaryList(CustomPrincipal customPrincipal, String keyword, Long learningLanguageId, Long learningLanguageLevelId) {

        // 스스로는 조회 제외
        UUID authenticatedUserUuid = customPrincipal.uuid();
        List<User> userList = userRepository.findAllByUuidNot(authenticatedUserUuid, keyword, learningLanguageId, learningLanguageLevelId);

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
    public void changeUserProfile(CustomPrincipal customPrincipal, MultipartFile multipartFile) {

        User user = userUtil.getUserByUuid(customPrincipal.uuid());

        String oldProfileImageUrl = user.getProfileImageUrl();
        String newProfileImageUrl = fileUtil.uploadFile(
                multipartFile,
                FilePurposeCode.PROFILE
        );

        user.changeUserProfileImageUrl(newProfileImageUrl);

        // TODO 로컬 이미지 삭제
        fileUtil.deleteFile(FilePurposeCode.PROFILE, oldProfileImageUrl);
    }

    @Transactional
    public void removeUser(CustomPrincipal customPrincipal) {

        UUID uuid = customPrincipal.uuid();
        User user = userUtil.getUserByUuid(uuid);
        String profileImageUrl = user.getProfileImageUrl();

        // 탈퇴 여부 확인
        if(user.getDeletedAt() != null) {
            CustomLogger.warn(
              LogEventCode.USER,
              "FAIL",
              LogEventReasonCode.DELETED_USER,
              LogEventReasonCode.DELETED_USER.getMessage(),
              uuid.toString()
            );
        }

        // 서비스 유저면 서비스 계정 삭제
        userUtil.deleteIfServiceAccountExist(uuid);

        // 소셜 유저면 소셜 계정 삭제
        userUtil.deleteIfSocialAccountExist(uuid);

        // 동의 약관 삭제
        termUtil.deleteAgreedTermList(uuid);

        // TODO 포스트, 좋아요, 댓글 삭제

        // TODO 게임 기록 삭제

        // 사용자 테이블의 식별 컬럼 null 처리
        user.softDeleteUser(userUtil.calculateAgeGroup(user.getBirth()));

        // TODO 유저 프로필 실제 삭제
        fileUtil.deleteFile(FilePurposeCode.PROFILE, profileImageUrl);
    }
}
