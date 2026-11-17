package com.languageworld.be.domain.vocab.repository;

import com.languageworld.be.domain.vocab.entity.SavedVocab;
import com.languageworld.be.domain.vocab.entity.VocabMeaning;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavedVocabRepository  extends JpaRepository<SavedVocab, Long> {

    @Query("""
        SELECT savedVocab
        FROM SavedVocab savedVocab
        WHERE savedVocab.user.uuid = :uuid
            AND LOWER(savedVocab.vocabMeaning.language.type) = LOWER(:learningLanguage)
            AND (:keyword IS NULL
            OR LOWER(savedVocab.vocabMeaning.meaning) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(savedVocab.memo) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
            AND (
                :cursor IS NULL
                OR savedVocab.id < :cursor
            )
        ORDER BY savedVocab.id DESC
    """)
    Slice<SavedVocab> findSlice(@Param("uuid") UUID userUuid , @Param("learningLanguage") String learningLanguage ,@Param("keyword") String keyword, Pageable pageable, Long cursor);

    Optional<SavedVocab> findByUser_UuidAndVocabMeaning_Id(UUID userUuid, Long vocabMeaningId);
}
