package com.languageworld.be.domain.language.service;

import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.language.repository.LanguageRepository;
import com.languageworld.be.domain.language.repository.LevelRepository;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.enumGroup.LogEventCode;
import com.languageworld.be.global.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final LevelRepository levelRepository;

    public Language getLanguageById(Long languageId) {

        Optional<Language> optionalLanguage = languageRepository.findById(languageId);
        if(optionalLanguage.isEmpty()) {
            CustomLogger.warn(LogEventCode.LANGUAGE,
                    "FAIL",
                    LogEventReasonCode.UNSUPPORTED_LANGUAGE,
                    LogEventReasonCode.UNSUPPORTED_UPLOAD_PURPOSE_REQUEST.getMessage() + " - LanguageId: " + languageId,
                    null
            );

            throw new CustomException(CustomExceptionCode.UNSUPPORTED_LANGUAGE);
        }
        Language language = optionalLanguage.get();

        return language;
    }

    public Level getLevelById(Long levelId) {

        Optional<Level> optionalLevel = levelRepository.findById(levelId);
        if(optionalLevel.isEmpty()) {
            CustomLogger.warn(LogEventCode.LANGUAGE,
                    "FAIL",
                    LogEventReasonCode.LEVEL_NOT_FOUND,
                    LogEventReasonCode.LEVEL_NOT_FOUND.getMessage() + " - LevelId: " + levelId,
                    null
            );

            throw new CustomException(CustomExceptionCode.LEVEL_NOT_FOUND);
        }
        Level level = optionalLevel.get();

        return level;
    }

}
