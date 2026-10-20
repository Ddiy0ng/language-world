package com.languageworld.be.domain.term.repository;

import com.languageworld.be.domain.term.entity.AgreedTerm;
import com.languageworld.be.domain.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreedTermRepository extends JpaRepository<AgreedTerm, Long> {

    List<AgreedTerm> findAllByUser_Uuid(String uuid);
}
