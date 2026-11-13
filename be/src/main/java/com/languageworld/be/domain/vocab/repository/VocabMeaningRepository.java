package com.languageworld.be.domain.vocab.repository;

import com.languageworld.be.domain.vocab.entity.VocabMeaning;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VocabMeaningRepository extends JpaRepository<VocabMeaning, Long> {

    @Query("""
        SELECT vocabMeaning
        FROM VocabMeaning vocabMeaning
        WHERE LOWER(vocabMeaning.language.type) = :learningLanguage
          AND (
              :keyword IS NULL
              OR LOWER(vocabMeaning.meaning) LIKE CONCAT('%', :keyword, '%')
          )
          AND (
              :cursor IS NULL
              OR vocabMeaning.id < :cursor
          )
        ORDER BY vocabMeaning.id DESC
    """)
    Slice<VocabMeaning> findSlice(@Param("learningLanguage") String learningLanguage , @Param("keyword") String keyword, Pageable pageable, Long cursor);

    Optional<VocabMeaning> findByVocab_IdAndLanguage_Type(Long vocabId, String motherTongueType);
}
