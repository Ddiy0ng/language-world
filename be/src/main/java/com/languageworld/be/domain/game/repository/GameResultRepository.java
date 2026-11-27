package com.languageworld.be.domain.game.repository;

import com.languageworld.be.domain.game.entity.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, Long> {
}
