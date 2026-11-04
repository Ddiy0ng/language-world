package com.languageworld.be.domain.post.repository;

import com.languageworld.be.domain.post.entity.Like;
import com.languageworld.be.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query(
        """
            SELECT like.id
            FROM Like like
            WHERE like.post.id = :id
                AND like.author.uuid = :uuid
        """
    )
    Optional<Long> findIdByPost_IdAndAuthor_Uuid(@Param("id") Long postId, @Param("uuid") UUID userUuid);

    @Query(
        """
            SELECT like
            FROM Like like
            WHERE like.author.uuid = :uuid
            ORDER BY like.id DESC
        """
    )
    Slice<Like> findFirstSlice(@Param("uuid") UUID userUuid, Pageable pageable);

    @Query(
        """
            SELECT like
            FROM Like like
            WHERE like.author.uuid = :uuid
                AND like.id < :cursor
            ORDER BY like.id DESC
        """
    )
    Slice<Like> findNextSlice(@Param("uuid") UUID userUuid, Pageable pageable, Long cursor);

    boolean existsByAuthor_UuidAndPost_Id(UUID userUuid, Long postId);

    void deleteAllByAuthor(User user);
}
