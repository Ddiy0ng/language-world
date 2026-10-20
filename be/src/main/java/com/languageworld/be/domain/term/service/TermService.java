package com.languageworld.be.domain.term.service;

import com.languageworld.be.domain.term.dto.response.AgreedTermResponseDto;
import com.languageworld.be.domain.term.dto.response.TermResponseDto;
import com.languageworld.be.domain.term.dto.request.TermRequestDto;
import com.languageworld.be.domain.term.entity.AgreedTerm;
import com.languageworld.be.domain.term.entity.Term;
import com.languageworld.be.domain.term.repository.AgreedTermRepository;
import com.languageworld.be.domain.term.repository.TermRepository;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.auth.enumGroup.UserRoleCode;
import com.languageworld.be.global.auth.service.AuthUtil;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.multiPartFileUtil.enumGroup.FilePurposeCode;
import com.languageworld.be.global.multiPartFileUtil.FileUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;
    private final AgreedTermRepository agreedTermRepository;
    private final FileUtil fileUtil;
    private final AuthUtil authUtil;

    @Transactional
    public void createTerm(CustomPrincipal customPrincipal, MultipartFile pdf, TermRequestDto.TermCreate termRequestDto) {

        // admin 권한 확인
        authUtil.adminAuthorizationCheck(customPrincipal);

        // 버전명
        String version = termRequestDto.purpose() + LocalDate.now().toString();

        //동일 약관 존재 확인(목적 + 버전)
        boolean isTermExist = termRepository.existsByVersion(version);
        if(isTermExist) {
            CustomLogger.warn(LogEventCode.TERM,
                    "FAIL",
                    LogEventReasonCode.TERM_ALREADY_EXIST,
                    LogEventReasonCode.TERM_ALREADY_EXIST.getMessage() + " - Version: " + version,
                    null);

            throw new CustomException(CustomExceptionCode.TERM_ALREADY_EXIST);
        }

        // 파일 업로드
        String fileUploadedPath = fileUtil.uploadFile(pdf, FilePurposeCode.TERM);

        // term 객체 생성
        Term term = Term.of(termRequestDto, fileUploadedPath, version);

        // 저장
        termRepository.save(term);
    }

    // TODO 약관 조회(ADMIN): active인 서비스 이용, 개인정보 수집 약관 조회
    @Transactional
    public List<TermResponseDto> getActiveTerm() {

        // active 상태인 term 조회
        List<Term> termList = termRepository.findAllByIsActive(true);
        List<TermResponseDto> termResponseDtoList =
                termList.stream()
                        .map(TermResponseDto::from)
                        .toList();

        return termResponseDtoList;
    }

    // TODO 약관 조회(USER): 동의한 약관 확인
    @Transactional
    public List<AgreedTermResponseDto> getAgreedTermList(CustomPrincipal customPrincipal) {

        // 인증 사용자가 동의한 agreedTerm 조회
        List<AgreedTerm> termList = agreedTermRepository.findAllByUser_Uuid(customPrincipal.uuid());
        List<AgreedTermResponseDto> agreedTermResponseDtoList =
                termList.stream()
                        .map(AgreedTermResponseDto::from)
                        .toList();

        return agreedTermResponseDtoList;
    }

    // TODO 약관 상태 변경(ADMIN): Active/Inactive
    @Transactional
    public void updateTermStatus(CustomPrincipal customPrincipal, Long termId, TermRequestDto.TermStatusUpdate termRequestDto) {

        // admin 권한 확인
        authUtil.adminAuthorizationCheck(customPrincipal);

        // 상태 변경
        Term term = getTermById(termId);
        if(termRequestDto.changeIntoStatus())
            term.activate();
        else
            term.deActivate();
    }

    // TODO 약관 동의 여부 변경(USER): 동의 철회
    @Transactional
    public void updateAgreedTermIsAgreed(Long agreedTermId, TermRequestDto.AgreedTermIsAgreedUpdate termRequestDto) {

        AgreedTerm agreedTerm = getAgreedTermById(agreedTermId);

        // agreedTerm 삭제
        agreedTermRepository.delete(agreedTerm);
    }

    // TODO 약관 삭제
    @Transactional
    public void deleteTerm(CustomPrincipal customPrincipal, Long termId) {

        authUtil.adminAuthorizationCheck(customPrincipal);

        Term term = getTermById(termId);

        termRepository.delete(term);
    }

    @Transactional
    public Term getTermById(Long termId) {

        Optional<Term> optionalTerm = termRepository.findById(termId);
        if(optionalTerm.isEmpty()) {
            CustomLogger.warn(LogEventCode.TERM,
                    "FAIL",
                    LogEventReasonCode.TERM_NOT_FOUND,
                    LogEventReasonCode.TERM_NOT_FOUND.getMessage() + " - TermId: " + termId,
                    null
            );

            throw new CustomException(CustomExceptionCode.TERM_NOT_FOUND);
        }
        Term term = optionalTerm.get();

        return term;
    }

    @Transactional
    private AgreedTerm getAgreedTermById(Long agreedTermId) {

        Optional<AgreedTerm> optionalAgreedTerm = agreedTermRepository.findById(agreedTermId);
        if(optionalAgreedTerm.isEmpty()) {
            CustomLogger.warn(LogEventCode.TERM,
                    "FAIL",
                    LogEventReasonCode.AGREED_TERM_NOT_FOUND,
                    LogEventReasonCode.AGREED_TERM_NOT_FOUND.getMessage() + " - TermId: " + agreedTermId,
                    null
            );

            throw new CustomException(CustomExceptionCode.AGREED_TERM_NOT_FOUND);
        }

        AgreedTerm  agreedTerm = optionalAgreedTerm.get();

        return agreedTerm;
    }

}
