package com.languageworld.be.domain.term.service;

import com.languageworld.be.domain.term.entity.AgreedTerm;
import com.languageworld.be.domain.term.entity.Term;
import com.languageworld.be.domain.term.enumGroup.TermPurposeCode;
import com.languageworld.be.domain.term.repository.AgreedTermRepository;
import com.languageworld.be.domain.term.repository.TermRepository;
import com.languageworld.be.global.auth.enumGroup.UserRoleCode;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TermUtil {

    private final TermRepository termRepository;
    private final AgreedTermRepository agreedTermRepository;

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

    public void termPurposeCheck(Term term, TermPurposeCode termPurposeCode) {

        if(term.getPurpose() != termPurposeCode) {

            CustomLogger.warn(LogEventCode.TERM,
                    "FAIL",
                    LogEventReasonCode.UNMATCHED_TERM_PURPOSE,
                    LogEventReasonCode.UNMATCHED_TERM_PURPOSE.getMessage() + " - 요청 약관 목적 및 ID: " + term.getPurpose() + term.getId() + " - 목표 목적: " + termPurposeCode,
                    null
            );

            throw new CustomException(CustomExceptionCode.UNMATCHED_TERM_PURPOSE);
        }
    }

    protected AgreedTerm getAgreedTermById(Long agreedTermId) {

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

    protected Term getTermByPurpose(TermPurposeCode termPurposeCode) {

        Optional<Term> optionalTerm = termRepository.findByIsActiveAndPurpose(true, termPurposeCode);

        if(optionalTerm.isEmpty()) {
            CustomLogger.warn(LogEventCode.TERM,
                    "FAIL",
                    LogEventReasonCode.TERM_NOT_FOUND,
                    LogEventReasonCode.TERM_NOT_FOUND.getMessage(),
                    null
            );

            throw new CustomException(CustomExceptionCode.TERM_NOT_FOUND);
        }
        Term term = optionalTerm.get();

        return term;
    }

    // 약관 주인 확인
    protected void agreedTermOwnerCheck(UUID uuid, AgreedTerm agreedTerm) {

        UUID ownerUuid = agreedTerm.getUser().getUuid();

        if(!uuid.equals(ownerUuid)) {

            CustomLogger.warn(LogEventCode.TERM,
                    "FAIL",
                    LogEventReasonCode.NOT_AGREED_TERM_OWNER,
                    LogEventReasonCode.NOT_AGREED_TERM_OWNER.getMessage() + " - TermId: " + agreedTerm.getId() + " - triedUserUuid: " + uuid,
                    null
            );

            throw new CustomException(CustomExceptionCode.NOT_AGREED_TERM_OWNER);
        }
    }

    public boolean isTermPurposeCorrect(Term term, TermPurposeCode termPurposeCode) {

        return term.getPurpose() == termPurposeCode;
    }

    public UserRoleCode validateTermCheck(List<AgreedTerm> agreedTermList) {

        List<Term> activeTermList = termRepository.findAllByIsActive(true);

        boolean isServiceTermAgreed = false;
        boolean isPersonalInfoTermAgreed = false;

        for(Term activeTerm : activeTermList) {

            for(AgreedTerm agreedTerm : agreedTermList) {

                Term term = agreedTerm.getTerm();

                if(activeTerm.getId().equals(term.getId()) && agreedTerm.isAgreed()) {
                    if (isTermPurposeCorrect(term, TermPurposeCode.SERVICE_USE)) {
                        isServiceTermAgreed = true;
                        break;
                    } else if (isTermPurposeCorrect(term, TermPurposeCode.PERSONAL_INFO_USE)) {
                        isPersonalInfoTermAgreed = true;
                        break;
                    }
                }
            }
        }

        if(!isServiceTermAgreed || !isPersonalInfoTermAgreed) {

            CustomLogger.warn(LogEventCode.TERM,
                    "FAIL",
                    LogEventReasonCode.REQUIRED_TERM_NOT_AGREED,
                    LogEventReasonCode.REQUIRED_TERM_NOT_AGREED.getMessage(),
                    null
            );

            return UserRoleCode.SERVICE_FORBIDDEN_USER;
        }

        return UserRoleCode.USER;
    }

    public void deleteAgreedTermList(UUID uuid) {

        agreedTermRepository.deleteAllByUser_Uuid(uuid);
    }
}
