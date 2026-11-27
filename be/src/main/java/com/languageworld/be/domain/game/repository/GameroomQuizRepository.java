package com.languageworld.be.domain.game.repository;

import com.languageworld.be.domain.game.entity.GameroomQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameroomQuizRepository extends JpaRepository<GameroomQuiz, Long> {
}
