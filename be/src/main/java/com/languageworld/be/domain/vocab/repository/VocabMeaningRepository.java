package com.languageworld.be.domain.vocab.repository;

import com.languageworld.be.domain.vocab.entity.VocabMeaning;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface VocabMeaningRepository extends JpaRepository<VocabMeaning, Long> {

    @Query("""
        select vocabMeaning.id
        from VocabMeaning vocabMeaning
        where vocabMeaning.language.id = :languageId and vocabMeaning.level.id = :levelId
    """)
    List<Long> findIdListByLanguageAndLevel(Long languageId, Long levelId);

    // 출제용: 연관(vocab·language·level)까지 쿼리 한 번에
    @EntityGraph(attributePaths = {"vocab", "language", "level"})
    List<VocabMeaning> findAllByIdIn(Collection<Long> vocabMeaningIdList);
}
