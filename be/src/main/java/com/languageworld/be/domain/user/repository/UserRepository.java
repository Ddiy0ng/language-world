package com.languageworld.be.domain.user.repository;

import com.languageworld.be.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(UUID uuid);

    @Query(
            """
                SELECT user
                FROM User user
                JOIN user.learningLanguage language
                JOIN user.learningLanguageLevel level
                WHERE user.uuid <> :authenticatedUserUuid
                    AND (
                        :keyword IS NULL
                        OR LOWER(user.name)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
                    AND (
                        :languageId IS NULL
                        OR language.id = :languageId
                    )
                    AND (
                        :levelId IS NULL
                        OR level.id = :levelId
                    )
            """
    )
    List<User> findAllByUuidNot(
            @Param("authenticatedUserUuid") UUID authenticatedUserUuid,
            @Param("keyword") String keyword,
            @Param("languageId") Long learningLanguageId,
            @Param("levelId") Long learningLanguageLevelId
            );
}
