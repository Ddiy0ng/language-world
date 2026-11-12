package com.languageworld.be.global.auth.service;

import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.language.service.LanguageUtil;
import com.languageworld.be.domain.post.repository.CommentRepository;
import com.languageworld.be.domain.post.repository.LikeRepository;
import com.languageworld.be.domain.post.repository.PostRepository;
import com.languageworld.be.domain.term.entity.AgreedTerm;
import com.languageworld.be.domain.term.entity.Term;
import com.languageworld.be.domain.term.enumGroup.TermPurposeCode;
import com.languageworld.be.domain.term.repository.AgreedTermRepository;
import com.languageworld.be.domain.term.service.TermUtil;
import com.languageworld.be.domain.user.entity.ServiceAccount;
import com.languageworld.be.domain.user.entity.SocialAccount;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.repository.*;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.global.auth.dto.request.*;
import com.languageworld.be.global.auth.dto.response.LoginResponseDto;
import com.languageworld.be.global.auth.dto.response.TokenReissueResponseDto;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.auth.entity.RefreshToken;
import com.languageworld.be.global.auth.entity.SocialAuthorizedUser;
import com.languageworld.be.global.auth.enumGroup.SocialTypeCode;
import com.languageworld.be.global.auth.enumGroup.UserRoleCode;
import com.languageworld.be.global.auth.repository.RefreshTokenRepository;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.jwt.JwtUtil;
import com.languageworld.be.global.multiPartFile.FileUtil;
import com.languageworld.be.global.multiPartFile.enumGroup.FilePurposeCode;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.jwt.JwtProvider;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ServiceAccountRepository serviceAccountRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AgreedTermRepository agreedTermRepository;
    private final FriendRepository friendRepository;
    private final BlockedUserRepository blockedUserRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final LanguageUtil languageUtil;
    private final TermUtil termUtil;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final AuthUtil authUtil;
    private final UserUtil userUtil;
    private final FileUtil fileUtil;
    private final RefreshTokenUtil refreshTokenUtil;
    private final JwtProvider jwtProvider;

    // 서비스 자체 회원가입
    @Transactional
    public void signup(ServiceSignupRequestDto serviceSignupRequestDto) {

        // 이메일로 가입 여부 확인(중복 가입 방지)
        String email = serviceSignupRequestDto.email();
        boolean isUserExist = serviceAccountRepository.existsByEmail(email);
        if(isUserExist) {
            CustomLogger.warn(
                    LogEventCode.AUTH,
                    "FAIL",
                    LogEventReasonCode.EMAIL_ALREADY_EXIST,
                    LogEventReasonCode.EMAIL_ALREADY_EXIST.getMessage() + " - Requested email: " + email,
                    null
            );

            throw new CustomException(CustomExceptionCode.EMAIL_ALREADY_EXIST);
        }

        // 비밀번호 유효성 검사
        String password = serviceSignupRequestDto.password();
        passwordUtil.validatePassword(password);

        // 비밀번호 암호화
        String encodedPassword = passwordUtil.encodePassword(password);

        // 참조 객체
        Language motherTongue = languageUtil.getLanguageById(serviceSignupRequestDto.motherTongueId());
        Language learningLanguage = languageUtil.getLanguageById(serviceSignupRequestDto.learningLanguageId());
        Level learningLanguageLevel = languageUtil.getLevelById(serviceSignupRequestDto.learningLanguageLevelId());
        if(!learningLanguage.equals(learningLanguageLevel.getLanguage())) {

            CustomLogger.warn(
                    LogEventCode.AUTH,
                    "FAIL",
                    LogEventReasonCode.NOT_PROPER_LEVEL_FOR_LANGUAGE,
                    LogEventReasonCode.NOT_PROPER_LEVEL_FOR_LANGUAGE.getMessage() + " - LearningLanguage: " + learningLanguage.getType() + ", Level: " + learningLanguageLevel.getLevel(),
                    null
            );

            throw new CustomException(CustomExceptionCode.NOT_PROPER_LEVEL_FOR_LANGUAGE);
        }

        Term serviceUseTerm = termUtil.getTermById(serviceSignupRequestDto.serviceUseTermId());
        termUtil.termPurposeCheck(serviceUseTerm, TermPurposeCode.SERVICE_USE);
        Term personalInfoUseTerm = termUtil.getTermById(serviceSignupRequestDto.personalInfoUseTermId());
        termUtil.termPurposeCheck(personalInfoUseTerm, TermPurposeCode.PERSONAL_INFO_USE);

        // 회원가입: user + account + term
        User user = User.of(serviceSignupRequestDto, motherTongue, learningLanguage, learningLanguageLevel);
        ServiceAccount serviceAccount = ServiceAccount.of(serviceSignupRequestDto.email(), encodedPassword, user);
        AgreedTerm agreedServiceTerm = AgreedTerm.of(serviceSignupRequestDto.isServiceUseTermAgreed(), user, serviceUseTerm);
        AgreedTerm agreedPersonalInfoUseTerm = AgreedTerm.of(serviceSignupRequestDto.isPersonalInfoUseTermAgreed(), user, personalInfoUseTerm);

        // 저장
        userRepository.save(user);
        serviceAccountRepository.save(serviceAccount);
        agreedTermRepository.save(agreedServiceTerm);
        agreedTermRepository.save(agreedPersonalInfoUseTerm);
        CustomLogger.info(
                LogEventCode.AUTH,
                "SUCCESS",
                SuccessCode.SIGNUP_SUCCESS.getMessage() + " - userUuid: " + user.getUuid(),
                null
        );
    }

    // 서비스 자체 로그인
    @Transactional
    public LoginResponseDto login(ServiceLoginRequestDto serviceLoginRequestDto) {

        // 사용자 존재 확인
        String email = serviceLoginRequestDto.email();
        Optional<ServiceAccount> optionalServiceAccount = serviceAccountRepository.findByEmail(email);
        if(optionalServiceAccount.isEmpty()) {
            CustomLogger.warn(
                    LogEventCode.AUTH,
                    "FAIL",
                    LogEventReasonCode.EMAIL_NOT_FOUND,
                    LogEventReasonCode.EMAIL_NOT_FOUND.getMessage() + " - Requested email: " +email,
                    null
            );

            throw new CustomException(CustomExceptionCode.LOGIN_FAILED);
        }
        ServiceAccount serviceAccount = optionalServiceAccount.get();

        // 비밀번호 일치 검사
        String rawPassword = serviceLoginRequestDto.password();
        String encodedPassword = serviceAccount.getPassword();
        if(!passwordUtil.isPasswordMatches(rawPassword, encodedPassword)) {

            CustomLogger.warn(
                    LogEventCode.AUTH,
                    "FAIL",
                    LogEventReasonCode.PASSWORD_MISMATCH,
                    LogEventReasonCode.PASSWORD_MISMATCH.getMessage() + " - Requested email: " + email,
                    null
            );
            throw new CustomException(CustomExceptionCode.LOGIN_FAILED);
        }

        // 사용자 데이터 조회
        User user = serviceAccount.getUser();

        if(user.getUserRole() != UserRoleCode.ADMIN)
            authUtil.userRoleChangeAccordingToTerm(user);

        // 로그인
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        RefreshToken refreshTokenEntity = RefreshToken.of(jwtUtil.getValidateClaimsFromToken(JwtUtil.REFRESH, refreshToken), user);
        refreshTokenRepository.save(refreshTokenEntity);

        LoginResponseDto loginResponseDto = LoginResponseDto.of(accessToken, refreshToken);
        CustomLogger.info(
                LogEventCode.AUTH,
                "SUCCESS",
                SuccessCode.LOGIN_SUCCESS.getMessage() + " - userUuid: " + user.getUuid(),
                null
        );

        return loginResponseDto;
    }

    // 소셜 회원가입
    @Transactional
    public void signup(String socialSignupType, SocialSignupRequestDto socialSignupRequestDto) {

        SocialTypeCode type = SocialTypeCode.from(socialSignupType);
        String token = socialSignupRequestDto.token();
        SocialAuthorizedUser socialAuthorizedUser = authUtil.getSocialAuthorizedUserBySocialType(type, token);

        // 소셜 파트
        String sub = socialAuthorizedUser.sub();
        String profileImageUrl = socialAuthorizedUser.profileImageUrl();

        // spring 파트
        //회원가입 여부 확인;
        authUtil.validateUserExistForSignup(type, sub);

        // 참조 객체
        Language motherTongue = languageUtil.getLanguageById(socialSignupRequestDto.motherTongueId());
        Language learningLanguage = languageUtil.getLanguageById(socialSignupRequestDto.learningLanguageId());
        Level learningLanguageLevel = languageUtil.getLevelById(socialSignupRequestDto.learningLanguageLevelId());
        if(!learningLanguage.equals(learningLanguageLevel.getLanguage())) {

            CustomLogger.warn(
                    LogEventCode.AUTH,
                    "FAIL",
                    LogEventReasonCode.NOT_PROPER_LEVEL_FOR_LANGUAGE,
                    LogEventReasonCode.NOT_PROPER_LEVEL_FOR_LANGUAGE.getMessage() + " - LearningLanguage: " + learningLanguage.getType() + ", Level: " + learningLanguageLevel.getLevel(),
                    null
            );

            throw new CustomException(CustomExceptionCode.NOT_PROPER_LEVEL_FOR_LANGUAGE);
        }

        Term serviceUseTerm = termUtil.getTermById(socialSignupRequestDto.serviceUseTermId());
        termUtil.termPurposeCheck(serviceUseTerm, TermPurposeCode.SERVICE_USE);
        Term personalInfoUseTerm = termUtil.getTermById(socialSignupRequestDto.personalInfoUseTermId());
        termUtil.termPurposeCheck(personalInfoUseTerm, TermPurposeCode.PERSONAL_INFO_USE);

        // 회원가입
        User user = User.of(socialSignupRequestDto, profileImageUrl, motherTongue, learningLanguage, learningLanguageLevel);
        SocialAccount socialAccount = SocialAccount.of(type, sub, user);
        AgreedTerm agreedServiceTerm = AgreedTerm.of(socialSignupRequestDto.isServiceUseTermAgreed(), user, serviceUseTerm);
        AgreedTerm agreedPersonalInfoUseTerm = AgreedTerm.of(socialSignupRequestDto.isPersonalInfoUseTermAgreed(), user, personalInfoUseTerm);

        // 저장
        userRepository.save(user);
        socialAccountRepository.save(socialAccount);
        agreedTermRepository.save(agreedServiceTerm);
        agreedTermRepository.save(agreedPersonalInfoUseTerm);
        CustomLogger.info(
                LogEventCode.AUTH,
                "SUCCESS",
                SuccessCode.SIGNUP_SUCCESS.getMessage() + " - userUuid: " + user.getUuid(),
                null
        );
    }

    // 소셜 로그인
    @Transactional
    public LoginResponseDto login(String socialLoginType, SocialLoginRequestDto socialLoginRequestDto) {

        SocialTypeCode type = SocialTypeCode.from(socialLoginType);
        String token = socialLoginRequestDto.token();
        SocialAuthorizedUser socialAuthorizedUser = authUtil.getSocialAuthorizedUserBySocialType(type, token);

        // 사용자 존재 확인
        String sub = socialAuthorizedUser.sub();
        SocialAccount socialAccount = authUtil.validateUserExistForLogin(type, sub);
        User user = socialAccount.getUser();

        // TODO 약관 동의 여부 확인(활성화된 약관)
        if(user.getUserRole() != UserRoleCode.ADMIN)
            authUtil.userRoleChangeAccordingToTerm(user);

        // 로그인
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        RefreshToken refreshTokenEntity = RefreshToken.of(jwtUtil.getValidateClaimsFromToken(JwtUtil.REFRESH, refreshToken), user);
        refreshTokenRepository.save(refreshTokenEntity);

        LoginResponseDto loginResponseDto = LoginResponseDto.of(accessToken, refreshToken);
        CustomLogger.info(
                LogEventCode.AUTH,
                "SUCCESS",
                SuccessCode.LOGIN_SUCCESS.getMessage() + " - userUuid: " + user.getUuid(),
                null
        );

        return loginResponseDto;
    }

    // 토큰 재발급
    @Transactional
    public TokenReissueResponseDto reissueToken(RefreshTokenRequestDto refreshTokenRequestDto) {

        Claims claims = jwtUtil.getValidateClaimsFromToken(JwtUtil.REFRESH, refreshTokenRequestDto.refreshToken());

        // 토큰 소유자 검증: jwt sub vs tokenId 기준 조회한 db에 저장된 토큰 소유자

        // user.getUuid: 요청 dto로 넘어온 token의 claims에서 추출한 id를 기준으로 db의 refresh token 찾아 추출한 소유자 user의 uuid
        String tokenId = claims.getId();
        RefreshToken oldRefreshToken = refreshTokenUtil.getRefreshToken(tokenId);
        User user = oldRefreshToken.getUser();

        // uuid: 요청dto로 넘어온 token의 claims에서 추출한 uuid
        UUID uuid = UUID.fromString(jwtUtil.getUuid(claims));
        if(!user.getUuid().equals(uuid)) {

            CustomLogger.warn(
                    LogEventCode.JWT,
                    "FAIL",
                    LogEventReasonCode.TOKEN_SUB_DB_UUID_NOT_SAME,
                    LogEventReasonCode.TOKEN_SUB_DB_UUID_NOT_SAME.getMessage()
                            + " - token sub uuid: " + uuid
                            + " - db token user uuid: " + user.getUuid(),
                    null
            );

            throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
        }

        // 리프리프레시 토큰 재사용 방지
        refreshTokenUtil.deletedRefreshTokenCheck(oldRefreshToken);

        // 만료 검증
        if(oldRefreshToken.getExpiredAt().before(new Date())) {

            CustomLogger.warn(
                    LogEventCode.JWT,
                    "FAIL",
                    LogEventReasonCode.EXPIRED_TOKEN,
                    LogEventReasonCode.EXPIRED_TOKEN.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.INVALID_TOKEN);
        }

        // 토큰 소유자 탈퇴 여부
        userUtil.deletedUserCheckByUserUuid(user.getUuid());

        // 새 토큰 발급
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);
        Claims newRefreshTokenClaim = jwtUtil.getValidateClaimsFromToken(JwtUtil.REFRESH, newRefreshToken);

        oldRefreshToken.removeToken();

        RefreshToken refreshTokenEntity = RefreshToken.of(newRefreshTokenClaim, user);
        refreshTokenRepository.save(refreshTokenEntity);

        return TokenReissueResponseDto.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(CustomPrincipal customPrincipal, RefreshTokenRequestDto refreshTokenRequestDto) {

        UUID userUuid = customPrincipal.uuid();

        Claims claims = jwtUtil.getValidateClaimsFromToken(JwtUtil.REFRESH, refreshTokenRequestDto.refreshToken());
        String tokenId = claims.getId();
        UUID tokenClaimUuid = UUID.fromString(claims.getSubject());

        RefreshToken refreshToken = refreshTokenUtil.getRefreshToken(tokenId);
        refreshTokenUtil.deletedRefreshTokenCheck(refreshToken);

        refreshTokenUtil.refreshTokenOwnerCheck(userUuid, tokenClaimUuid, refreshToken.getUser().getUuid());

        refreshToken.removeToken();
    }

    @Transactional
    public void removeUser(CustomPrincipal customPrincipal) {

        UUID uuid = customPrincipal.uuid();
        User user = userUtil.getUserByUuid(uuid);

        // 삭제할 경로 사라지기 전 먼저 커밋 시 삭제할 파일 리스트에 저장
        List<String> commitDeleteFilePathList = new ArrayList<>();
        fileUtil.registerCleanUp(FilePurposeCode.POST, commitDeleteFilePathList, List.of());
        String profileImageUrl = user.getProfileImageUrl();
        commitDeleteFilePathList.add(profileImageUrl);

        // 탈퇴 여부 확인
        if(user.getDeletedAt() != null) {
            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.DELETED_USER,
                    LogEventReasonCode.DELETED_USER.getMessage(),
                    uuid.toString()
            );

            throw new CustomException(CustomExceptionCode.DELETED_USER);
        }

        // TODO 리프레시 토큰 삭제
        refreshTokenRepository.findAllByUser(user).forEach(RefreshToken::removeToken);

        // 서비스 유저면 서비스 계정 삭제
        Optional<ServiceAccount> optionalServiceAccount = serviceAccountRepository.findByUser_Uuid(uuid);
        if(optionalServiceAccount.isPresent()) {

            serviceAccountRepository.delete(optionalServiceAccount.get());

            CustomLogger.info(
                    LogEventCode.USER,
                    "SUCCESS",
                    SuccessCode.SERVICE_ACCOUNT_REMOVE_SUCCESS.getMessage(),
                    uuid.toString()
            );
        }

        // 소셜 유저면 소셜 계정 삭제
        Optional<SocialAccount> optionalSocialAccount = socialAccountRepository.findByUser_Uuid(uuid);
        if(optionalSocialAccount.isPresent()) {

            socialAccountRepository.delete(optionalSocialAccount.get());

            CustomLogger.info(
                    LogEventCode.USER,
                    "SUCCESS",
                    SuccessCode.SOCIAL_ACCOUNT_REMOVE_SUCCESS.getMessage(),
                    uuid.toString()
            );
        }

        // 동의 약관 삭제
        agreedTermRepository.deleteAllByUser(user);

        // TODO 친구 관계 삭제, 차단 관계 삭제
        friendRepository.deleteAllByUserOrFriend(user,user);
        blockedUserRepository.deleteAllByUserOrBlockedUser(user, user);

        // TODO 포스트, 좋아요, 댓글 삭제
        likeRepository.deleteAllByAuthor(user);
        commentRepository.deleteAllByAuthor(user);
        postRepository.deleteAllByAuthor(user);

        // TODO 채팅 내역 삭제(모든 사용자 제거 시 또는 퇴장 시)

        // TODO 게임 기록 삭제

        // 사용자 테이블의 식별 컬럼 null 처리
        user.softDeleteUser(authUtil.calculateAgeGroup(user.getBirth()));

        // TODO 유저 프로필 실제 삭제
        fileUtil.deleteFile(FilePurposeCode.PROFILE, profileImageUrl);
    }
}