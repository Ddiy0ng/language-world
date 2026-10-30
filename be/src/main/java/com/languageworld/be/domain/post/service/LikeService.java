package com.languageworld.be.domain.post.service;

import com.languageworld.be.domain.post.dto.response.LikePostResponseDto;
import com.languageworld.be.domain.post.repository.LikeRepository;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    @Transactional
    public void createLike(CustomPrincipal customPrincipal, Long postId) {
    }

    @Transactional
    public List<LikePostResponseDto> findAuthenticatedUserLikePostList(CustomPrincipal customPrincipal) {
    }

    @Transactional
    public List<LikePostResponseDto> findSpecificUserLikePostList(CustomPrincipal customPrincipal, UUID userUuid) {
    }

    @Transactional
    public void removeLike(CustomPrincipal customPrincipal, Long likeId) {
    }
}
