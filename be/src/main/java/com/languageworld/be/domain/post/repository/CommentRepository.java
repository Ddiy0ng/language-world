package com.languageworld.be.domain.post.repository;

import com.languageworld.be.domain.post.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(
        """
            SELECT comment
            FROM Comment comment
            WHERE comment.author.uuid = :uuid
            ORDER BY comment.id DESC
        """
    )
    Slice<Comment> findFirstSlice(@Param("uuid") UUID userUuid, Pageable pageable);

    @Query(
            """
                SELECT comment
                FROM Comment comment
                WHERE comment.post.id = :id
                ORDER BY comment.id DESC
            """
    )
    Slice<Comment> findFirstSlice(@Param("id") Long postId, Pageable pageable);

    @Query(
            """
                SELECT comment
                FROM Comment comment
                WHERE comment.author.uuid = :uuid
                    AND comment.id < :cursor
                ORDER BY comment.id DESC
            """
    )
    Slice<Comment> findNextSlice(@Param("uuid") UUID userUuid, Pageable pageable, Long cursor);

    @Query(
            """
                SELECT comment
                FROM Comment comment
                WHERE comment.post.id = :id
                    AND comment.id < :cursor
                ORDER BY comment.id DESC
            """
    )
    Slice<Comment> findNextSlice(@Param("id") Long postId, Pageable pageable, Long cursor);
}
