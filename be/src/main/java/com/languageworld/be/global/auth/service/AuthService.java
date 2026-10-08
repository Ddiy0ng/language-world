package com.languageworld.be.global.auth.service;

import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.language.service.LanguageService;
import com.languageworld.be.domain.term.entity.AgreedTerm;
import com.languageworld.be.domain.term.entity.Term;
import com.languageworld.be.domain.term.repository.AgreedTermRepository;
import com.languageworld.be.domain.term.service.TermService;
import com.languageworld.be.domain.user.entity.ServiceAccount;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.repository.ServiceAccountRepository;
import com.languageworld.be.domain.user.repository.UserRepository;
import com.languageworld.be.global.auth.dto.LoginResponseDto;
import com.languageworld.be.global.auth.dto.ServiceLoginRequestDto;
import com.languageworld.be.global.auth.dto.ServiceSignupRequestDto;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.jwt.JwtProvider;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.enumGroup.LogEventCode;
import com.languageworld.be.global.enumGroup.LogEventReasonCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ServiceAccountRepository serviceAccountRepository;
    private final AgreedTermRepository agreedTermRepository;
    private final LanguageService languageService;
    private final TermService termService;
    private final AuthUtil authUtil;
    private final JwtProvider jwtProvider;

    // 서비스 자체 회원가입
    @Transactional
    public void signup(ServiceSignupRequestDto serviceSignupRequestDto) {

        // 이메일로 가입 여부 확인(중복 가입 방지)
        String email = serviceSignupRequestDto.email();
        boolean isUserExist = serviceAccountRepository.existsByEmail(email);
        if(isUserExist) {
            CustomLogger.warn(
                    LogEventCode.SIGNUP,
                    "FAIL",
                    LogEventReasonCode.EMAIL_ALREADY_EXIST,
                    LogEventReasonCode.EMAIL_ALREADY_EXIST.getMessage() + " - Requested email: " + email,
                    null
            );

            throw new CustomException(CustomExceptionCode.EMAIL_ALREADY_EXIST);
        }

        // 비밀번호 유효성 검사
        String password = serviceSignupRequestDto.password();
        authUtil.validatePassword(password);

        // 비밀번호 암호화
        String encodedPassword = authUtil.encodePassword(password);

        // 참조 객체
        Language motherTongue = languageService.getLanguageById(serviceSignupRequestDto.motherTongueId());
        Language learningLanguage = languageService.getLanguageById(serviceSignupRequestDto.learningLanguageId());
        Level learningLanguageLevel = languageService.getLevelById(serviceSignupRequestDto.learningLanguageLevelId());
        if(!learningLanguage.equals(learningLanguageLevel.getLanguage())) {

            CustomLogger.warn(
                    LogEventCode.SIGNUP,
                    "FAIL",
                    LogEventReasonCode.NOT_PROPER_LEVEL_FOR_LANGUAGE,
                    LogEventReasonCode.NOT_PROPER_LEVEL_FOR_LANGUAGE.getMessage() + " - LearningLanguage: " + learningLanguage.getType() + ", Level: " + learningLanguageLevel.getLevel(),
                    null
            );

            throw new CustomException(CustomExceptionCode.NOT_PROPER_LEVEL_FOR_LANGUAGE);
        }

        // 서비스 약관, 개인정보 수집 약관 확인 로직 추가 필요!!!

        Term serviceUseTerm = termService.getTermById(serviceSignupRequestDto.serviceUseTermId());
        Term personalInfoUseTerm = termService.getTermById(serviceSignupRequestDto.personalInfoUseTermId());

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
    }

//    // 소셜 회원가입
//    @Transactional
//    public void signup(SocialSignupRequestDto socialSignupRequestDto) {
//
//        // providerId로 가입 여부 확인(중복 가입 방지)
//
//        // 참조 객체
//        Language motherTongue = languageService.getLanguageById(serviceSignupRequestDto.motherTongueId());
//        Language learningLanguage = languageService.getLanguageById(serviceSignupRequestDto.learningLanguageId());
//        Level learningLanguageLevel = languageService.getLevelById(serviceSignupRequestDto.learningLanguageLevelId());
//
//        // 회원가입: user + account + term
//
//    }

    // 서비스 자체 로그인
    @Transactional
    public LoginResponseDto login(ServiceLoginRequestDto serviceLoginRequestDto) {

        // 사용자 존재 확인
        String email = serviceLoginRequestDto.email();
        Optional<ServiceAccount> optionalServiceAccount = serviceAccountRepository.findByEmail(email);
        if(optionalServiceAccount.isEmpty()) {
            CustomLogger.warn(
                    LogEventCode.LOGIN,
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
        if(!authUtil.isPasswordMatches(rawPassword, encodedPassword))
            throw new CustomException(CustomExceptionCode.LOGIN_FAILED);

        // 사용자 데이터 조회
        User user = serviceAccount.getUser();

        // 로그인
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        LoginResponseDto loginResponseDto = LoginResponseDto.of(accessToken, refreshToken);

        return loginResponseDto;
    }

    // 소셜 로그인
}