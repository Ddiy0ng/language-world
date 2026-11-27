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

import java.util.*;

@Component
@RequiredArgsConstructor
public class VocabMeaningUtil {

    private final VocabMeaningRepository vocabMeaningRepository;

    // 게임 출제 후보: 학습 언어·레벨에 해당하는 단어 id 목록, 하나도 없으면 방 생성 불가
    public List<Long> getGameQuizIdList(Long languageId, Long levelId, int roundSize, UUID userUuid) {

        List<Long> quizIdList = vocabMeaningRepository.findIdListByLanguageAndLevel(languageId, levelId);
        if(quizIdList.size() < roundSize) {
            CustomLogger.warn(LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.NOT_ENOUGH_GAME_QUIZ,
                    LogEventReasonCode.NOT_ENOUGH_GAME_QUIZ.getMessage()
                            + " - languageId: " + languageId + ", levelId: " + levelId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.NOT_ENOUGH_GAME_QUIZ);
        }

        return quizIdList;
    }

    // 이번 매치 문제: 연관까지 쿼리 한 번, DB 순서(보통 id 순)로 오므로 섞어서 출제 순서를 무작위로
    // (방 생성 뒤 삭제된 단어는 조회되지 않아 그만큼 문제 수가 줄어듦 → GameState.hasNextRound가 실제 문제 수 기준)
    public List<VocabMeaning> getVocabMeaningListByPickedQuizIdList(List<Long> pickedQuizIdList) {

        List<VocabMeaning> vocabMeaningList = new ArrayList<>(vocabMeaningRepository.findAllByIdIn(pickedQuizIdList));
        Collections.shuffle(vocabMeaningList);

        return vocabMeaningList;
    }

    // 출제 기록 저장용: 조회 쿼리 없이 FK만 거는 참조
    public VocabMeaning getVocabMeaningReference(Long vocabMeaningId) {return vocabMeaningRepository.getReferenceById(vocabMeaningId);}

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
