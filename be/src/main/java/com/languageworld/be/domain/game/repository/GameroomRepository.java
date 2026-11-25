package com.languageworld.be.domain.game.repository;

import com.languageworld.be.domain.game.entity.Gameroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameroomRepository extends JpaRepository<Gameroom, Long> {

    List<Gameroom> findAllByIdInOrderByIdDesc(List<Long> idList);
}