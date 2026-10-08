package com.languageworld.be.domain.term.repository;

import com.languageworld.be.domain.term.entity.Term;
import com.languageworld.be.domain.term.enumGroup.TermPurposeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    boolean existsByPurposeAndVersion(TermPurposeCode purpose, String version);
}
