package com.languageworld.be.domain.post.service;

import com.languageworld.be.domain.post.dto.request.CreateCommentRequestDto;
import com.languageworld.be.domain.post.dto.response.PostCommentResponseDto;
import com.languageworld.be.domain.post.dto.response.UserCommentResponseDto;
import com.languageworld.be.domain.post.repository.CommentRepository;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public void createComment(CustomPrincipal customPrincipal, Long postId, CreateCommentRequestDto createCommentRequestDto) {
    }

    @Transactional
    public List<UserCommentResponseDto> findAuthenticatedUserCommentList(CustomPrincipal customPrincipal) {
    }

    @Transactional
    public List<UserCommentResponseDto> findSpecificUserCommentList(CustomPrincipal customPrincipal, UUID userUuid) {
    }

    @Transactional
    public List<PostCommentResponseDto> findPostCommentList(CustomPrincipal customPrincipal, Long postId) {
    }

    @Transactional
    public void removeComment(CustomPrincipal customPrincipal, Long commentId) {
    }
}
