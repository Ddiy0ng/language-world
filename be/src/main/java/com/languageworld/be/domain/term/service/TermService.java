package com.languageworld.be.domain.term.service;

import com.languageworld.be.domain.term.dto.request.TermAgreeRequestDto;
import com.languageworld.be.domain.term.dto.request.TermCreateRequestDto;
import com.languageworld.be.domain.term.dto.request.TermActivenessUpdateRequestDto;
import com.languageworld.be.domain.term.dto.response.AgreedTermResponseDto;
import com.languageworld.be.domain.term.dto.response.TermResponseDto;
import com.languageworld.be.domain.term.entity.AgreedTerm;
import com.languageworld.be.domain.term.entity.Term;
import com.languageworld.be.domain.term.enumGroup.TermPurposeCode;
import com.languageworld.be.domain.term.repository.AgreedTermRepository;
import com.languageworld.be.domain.term.repository.TermRepository;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
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

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;
    private final AgreedTermRepository agreedTermRepository;
    private final FileUtil fileUtil;
    private final TermUtil termUtil;
    private final UserUtil userUtil;

    @Transactional
    public void createTerm(MultipartFile pdf, TermCreateRequestDto termCreateRequestDto) {

        // 버전명
        String version = termCreateRequestDto.purpose() + LocalDate.now().toString();

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
        Term term = Term.of(termCreateRequestDto, fileUploadedPath, version);

        // 저장
        termRepository.save(term);
    }

    @Transactional
    public void agreeTerm(CustomPrincipal customPrincipal, TermAgreeRequestDto termAgreeRequestDto) {

        User user = userUtil.getUserByUuid(customPrincipal.uuid());

        if(termAgreeRequestDto.serviceTermId() != null) {
            Term serviceTerm = termUtil.getTermById(termAgreeRequestDto.serviceTermId());
            termUtil.termPurposeCheck(serviceTerm, TermPurposeCode.SERVICE_USE);

            AgreedTerm agreedTerm = AgreedTerm.of(true, user, serviceTerm);

            agreedTermRepository.save(agreedTerm);
        }

        if(termAgreeRequestDto.personalInfoTermId() != null) {
            Term personalInfoTerm = termUtil.getTermById(termAgreeRequestDto.personalInfoTermId());
            termUtil.termPurposeCheck(personalInfoTerm, TermPurposeCode.PERSONAL_INFO_USE);

            AgreedTerm agreedTerm = AgreedTerm.of(true, user, personalInfoTerm);

            agreedTermRepository.save(agreedTerm);
        }


    };

    @Transactional
    public List<TermResponseDto> getAllTerm() {

        List<Term> termlist = termRepository.findAll();
        List<TermResponseDto> termResponseDtoList =
                termlist.stream()
                        .map(TermResponseDto::from)
                        .toList();

        return termResponseDtoList;
    }

    // TODO 약관 조회: active인 서비스 이용, 개인정보 수집 약관 조회
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
    public void updateTermActiveness(Long termId, TermActivenessUpdateRequestDto termActivenessUpdateRequestDto) {

        // 상태 변경
        Term term = termUtil.getTermById(termId);
        if(termActivenessUpdateRequestDto.changeIntoStatus())
            term.activate();
        else
            term.deActivate();
    }

    // TODO 약관 동의 철회(USER)
    @Transactional
    public void disAgreeAgreedTerm(CustomPrincipal customPrincipal, Long agreedTermId) {

        AgreedTerm agreedTerm = termUtil.getAgreedTermById(agreedTermId);

        // 본인이 동의한 약관인지 확인
        termUtil.agreedTermOwnerCheck(customPrincipal.uuid(), agreedTerm);

        // agreedTerm 동의 철회
        agreedTermRepository.delete(agreedTerm);
    }
}
