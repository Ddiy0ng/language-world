package com.languageworld.be.domain.game.repository;

import com.languageworld.be.domain.game.entity.Gameroom;
import com.languageworld.be.domain.game.enumGroup.GameroomStatusCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameroomRepository extends JpaRepository<Gameroom, Long> {

    List<Gameroom> findAllByIdInAndGameroomStatusNotOrderByIdDesc(List<Long> idList, GameroomStatusCode gameroomStatusCode);
}