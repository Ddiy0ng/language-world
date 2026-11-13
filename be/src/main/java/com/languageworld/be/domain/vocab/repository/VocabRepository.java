package com.languageworld.be.domain.vocab.repository;

import com.languageworld.be.domain.vocab.entity.Vocab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabRepository  extends JpaRepository<Vocab, Long> {
}
