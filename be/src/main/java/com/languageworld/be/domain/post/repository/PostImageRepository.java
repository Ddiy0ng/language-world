package com.languageworld.be.domain.post.repository;

import com.languageworld.be.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    Optional<PostImage> findByIdAndPost_Id(Long postImageId, Long postId);
}
