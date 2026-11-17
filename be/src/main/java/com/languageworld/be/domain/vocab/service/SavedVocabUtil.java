package com.languageworld.be.domain.vocab.service;

import com.languageworld.be.domain.vocab.entity.SavedVocab;
import com.languageworld.be.domain.vocab.repository.SavedVocabRepository;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SavedVocabUtil {

    private final SavedVocabRepository savedVocabRepository;

    protected SavedVocab getSavedVocabById(UUID userUuid, Long savedVocabId) {

        Optional<SavedVocab> optionalSavedVocab = savedVocabRepository.findById(savedVocabId);
        if(optionalSavedVocab.isEmpty()) {
            CustomLogger.warn(LogEventCode.VOCAB,
                    "FAIL",
                    LogEventReasonCode.SAVED_VOCAB_NOT_FOUND,
                    LogEventReasonCode.SAVED_VOCAB_NOT_FOUND.getMessage()
                            + " - savedVocabId: " + savedVocabId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.SAVED_VOCAB_NOT_FOUND);
        }

        return optionalSavedVocab.get();
    }

    protected void savedVocabOwnerCheck(SavedVocab savedVocab, UUID userUuid) {

        UUID savedVocabUserUuid = savedVocab.getUser().getUuid();
        if(!savedVocabUserUuid.equals(userUuid)) {
            CustomLogger.warn(LogEventCode.VOCAB,
                    "FAIL",
                    LogEventReasonCode.NOT_SAVED_VOCAB_OWNER,
                    LogEventReasonCode.NOT_SAVED_VOCAB_OWNER.getMessage()
                            + " - savedVocabId: " + savedVocab.getId()
                            + " - savedVocabUserUuid: " + savedVocabUserUuid,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.FORBIDDEN_SAVED_VOCAB_REQUEST);
        }
    }
}
