package com.languageworld.be.domain.vocab.repository;

import com.languageworld.be.domain.vocab.entity.SavedVocab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedVocabRepository  extends JpaRepository<SavedVocab, Long> {

}
