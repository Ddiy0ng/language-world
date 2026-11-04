package com.languageworld.be.domain.post.repository;

import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
import com.languageworld.be.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByAuthor_Uuid(UUID userUuid);

    @Query(
            """
                SELECT post
                FROM Post post
                WHERE (
                    :type IS NULL
                    OR post.postType = :type
                    )
                    AND (
                        :keyword IS NULL
                        OR LOWER(post.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(post.contentText) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
                ORDER BY post.id DESC
            """
    )
    Slice<Post> findFirstSlice(@Param("type") PostTypeCode postTypeCode, String keyword, Pageable pageable);

    @Query(
            """
                SELECT post
                FROM Post post
                WHERE post.author.uuid = :uuid
                    AND (
                        :type IS NULL
                        OR post.postType = :type
                    )
                    AND (
                        :keyword IS NULL
                        OR LOWER(post.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(post.contentText) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
                ORDER BY post.id DESC
            """
    )
    Slice<Post> findFirstSlice(@Param("uuid") UUID userUuid, @Param("type") PostTypeCode postTypeCode, String keyword, Pageable pageable);

    @Query(
            """
                SELECT post
                FROM Post post
                WHERE post.id < :cursor
                    AND (
                        :type IS NULL
                        OR post.postType = :type
                    )
                    AND (
                        :keyword IS NULL
                        OR LOWER(post.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(post.contentText) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
                ORDER BY post.id DESC
            """
    )
    Slice<Post> findNextSlice(@Param("type") PostTypeCode postTypeCode, String keyword, Pageable pageable, Long cursor);

    @Query(
            """
                SELECT post
                FROM Post post
                WHERE post.id < :cursor
                    AND post.author.uuid = :uuid
                    AND (
                        :type IS NULL
                        OR post.postType = :type
                    )
                    AND (
                        :keyword IS NULL
                        OR LOWER(post.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(post.contentText) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
                ORDER BY post.id DESC
            """
    )
    Slice<Post> findNextSlice(@Param("uuid") UUID userUuid, @Param("type") PostTypeCode postTypeCode, String keyword, Pageable pageable, Long cursor);

    void deleteAllByAuthor(User user);
}
