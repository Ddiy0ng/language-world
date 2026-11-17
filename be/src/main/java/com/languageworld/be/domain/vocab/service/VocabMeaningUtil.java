package com.languageworld.be.domain.vocab.service;

import com.languageworld.be.domain.vocab.entity.VocabMeaning;
import com.languageworld.be.domain.vocab.repository.VocabMeaningRepository;
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
public class VocabMeaningUtil {

    private final VocabMeaningRepository vocabMeaningRepository;

    public VocabMeaning getVocabMeaningByVocabMeaningId(UUID userUuid, Long vocabMeaningId) {

        Optional<VocabMeaning> optionalVocabMeaning = vocabMeaningRepository.findById(vocabMeaningId);
        if(optionalVocabMeaning.isEmpty()) {
            CustomLogger.warn(LogEventCode.VOCAB,
                    "FAIL",
                    LogEventReasonCode.VOCAB_MEANING_NOT_FOUND,
                    LogEventReasonCode.VOCAB_MEANING_NOT_FOUND.getMessage()
                            + " - vocabMeaningId: " + vocabMeaningId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.VOCAB_MEANING_NOT_FOUND);
        }

        return optionalVocabMeaning.get();
    }
}
