package com.languageworld.be.domain.vocab.repository;

import com.languageworld.be.domain.vocab.entity.VocabMeaning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabMeaningRepository extends JpaRepository<VocabMeaning, Long> {

}
