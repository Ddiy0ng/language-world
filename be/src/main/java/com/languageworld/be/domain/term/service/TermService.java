package com.languageworld.be.domain.term.service;

import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.term.dto.TermCreateRequestDto;
import com.languageworld.be.domain.term.entity.Term;
import com.languageworld.be.domain.term.repository.TermRepository;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.enumGroup.LogEventCode;
import com.languageworld.be.global.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.multiPartFileUtil.FilePurposeCode;
import com.languageworld.be.global.multiPartFileUtil.FileUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;
    private final FileUtil fileUtil;

    @Transactional
    public void createTerm(MultipartFile pdf, TermCreateRequestDto termCreateRequestDto) {

        String version = termCreateRequestDto.purpose() + LocalDate.now().toString();

        //동일 약관 존재 확인(목적 + 버전)
        boolean isTermExist = termRepository.existsByPurposeAndVersion(termCreateRequestDto.purpose(), version);
        if(isTermExist) {
            CustomLogger.warn(LogEventCode.TERM,
                    "FAIL",
                    LogEventReasonCode.TERM_ALREADY_EXIST,
                    null);

            throw new CustomException(CustomExceptionCode.TERM_ALREADY_EXIST);
        }

        String fileUploadedPath = fileUtil.uploadFile(pdf, FilePurposeCode.TERM);

        Term term = Term.of(termCreateRequestDto, fileUploadedPath, version);

        termRepository.save(term);
    }

    public Term getTermById(Long termId) {

        Optional<Term> optionalTerm = termRepository.findById(termId);
        if(optionalTerm.isEmpty()) {
            CustomLogger.warn(LogEventCode.TERM,
                    "FAIL",
                    LogEventReasonCode.TERM_NOT_FOUND,
                    null
            );

            throw new CustomException(CustomExceptionCode.TERM_NOT_FOUND);
        }
        Term term = optionalTerm.get();

        return term;
    }
}
