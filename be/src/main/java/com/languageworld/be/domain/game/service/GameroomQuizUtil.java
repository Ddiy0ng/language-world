package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.entity.GameroomQuiz;
import com.languageworld.be.domain.game.entity.Quiz;
import com.languageworld.be.domain.game.repository.GameroomQuizRepository;
import com.languageworld.be.domain.vocab.service.VocabMeaningUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameroomQuizUtil {

    private final VocabMeaningUtil vocabMeaningUtil;
    private final GameroomQuizRepository gameroomQuizRepository;

    // 실제 출제된(시작된 라운드의) 퀴즈만 저장
    public void saveGameroomQuiz(GameState gameState) {

        List<Quiz> quizList = gameState.getQuizList();

        // 실제 퀴즈 엔티티 리스트
        List<GameroomQuiz> gameroomQuizList = new ArrayList<>();
        for(int i = 0; i < quizList.size(); i++) {

            Quiz quiz = quizList.get(i);

            // 라운드는 순서대로 시작되므로 출제 안 된 문제가 나오면 그 뒤도 전부 출제 안 됨
            if(!quiz.isSubmitted())
                break;

            gameroomQuizList.add(GameroomQuiz.of(
                    gameState.getMatchUuid(),
                    i + 1,
                    vocabMeaningUtil.getVocabMeaningReference(quiz.getVocabMeaningId())));
        }

        if(!gameroomQuizList.isEmpty())
            gameroomQuizRepository.saveAll(gameroomQuizList);
    }
}
