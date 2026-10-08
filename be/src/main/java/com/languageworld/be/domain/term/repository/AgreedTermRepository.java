package com.languageworld.be.domain.term.repository;

import com.languageworld.be.domain.term.entity.AgreedTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreedTermRepository extends JpaRepository<AgreedTerm, Long> {
}
