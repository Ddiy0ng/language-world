package com.languageworld.be.domain.term.repository;

import com.languageworld.be.domain.term.entity.Term;
import com.languageworld.be.domain.term.enumGroup.TermPurposeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {

    boolean existsByVersion(String version);

    List<Term> findAllByIsActive(boolean b);
}
