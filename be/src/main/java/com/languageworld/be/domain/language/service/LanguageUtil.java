package com.languageworld.be.domain.language.service;

import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.language.repository.LanguageRepository;
import com.languageworld.be.domain.language.repository.LevelRepository;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageUtil {

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

        return optionalLanguage.get();
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

        return optionalLevel.get();
    }

    // 게임 결과 저장용: 조회 쿼리 없이 FK만 거는 참조
    public Language getLanguageReference(Long languageId) {return languageRepository.getReferenceById(languageId);}

    public Level getLevelReference(Long levelId) {return levelRepository.getReferenceById(levelId);}

    public void languageLevelMatchCheck(Language language, Level level, UUID userUuid) {

        if(!level.getLanguage().getId().equals(language.getId())) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.GAMEROOM_LEVEL_LANGUAGE_MISMATCH,
                    LogEventReasonCode.GAMEROOM_LEVEL_LANGUAGE_MISMATCH.getMessage()
                            + " - languageId: " + language.getId()
                            + ", levelId: " + level.getId(),
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.GAMEROOM_LEVEL_LANGUAGE_MISMATCH);
        }
    }

}
