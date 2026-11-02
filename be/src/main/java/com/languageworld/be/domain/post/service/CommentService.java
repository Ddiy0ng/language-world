package com.languageworld.be.domain.post.service;

import com.languageworld.be.domain.post.dto.request.CommentCreateRequestDto;
import com.languageworld.be.domain.post.dto.request.CommentUpdateRequestDto;
import com.languageworld.be.domain.post.dto.response.PostCommentResponseDto;
import com.languageworld.be.domain.post.dto.response.UserCommentResponseDto;
import com.languageworld.be.domain.post.entity.Comment;
import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
import com.languageworld.be.domain.post.repository.CommentRepository;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserUtil userUtil;
    private final PostUtil postUtil;
    private final CommentUtil commentUtil;

    private final int SIZE = 10;

    @Transactional
    public void createComment(CustomPrincipal customPrincipal, Long postId, CommentCreateRequestDto commentCreateRequestDto) {

        UUID userUuid = customPrincipal.uuid();

        User author = userUtil.getUserByUuid(userUuid);

        Post post = postUtil.getPostByPostId(userUuid, postId);

        Comment comment = Comment.of(commentCreateRequestDto, author, post);

        commentRepository.save(comment);
    }

    @Transactional
    public List<UserCommentResponseDto> findAuthenticatedUserCommentList(CustomPrincipal customPrincipal, Long cursor) {

        UUID userUuid = customPrincipal.uuid();

        Slice<Comment> commentSlice = findComment(userUuid, null, cursor);
        List<UserCommentResponseDto> userCommentResponseDtoList = commentSlice.getContent().stream()
                .map(UserCommentResponseDto::from)
                .toList();

        return userCommentResponseDtoList;
    }

    @Transactional
    public List<UserCommentResponseDto> findSpecificUserCommentList(UUID userUuid, Long cursor) {

        Slice<Comment> commentSlice = findComment(userUuid, null, cursor);
        List<UserCommentResponseDto> userCommentResponseDtoList = commentSlice.getContent().stream()
                .map(UserCommentResponseDto::from)
                .toList();

        return userCommentResponseDtoList;
    }

    @Transactional
    public List<PostCommentResponseDto> findPostCommentList(Long postId, Long cursor) {

        Slice<Comment> commentSlice = findComment(null, postId, cursor);
        List<PostCommentResponseDto> userCommentResponseDtoList = commentSlice.getContent().stream()
                .map(PostCommentResponseDto::from)
                .toList();

        return userCommentResponseDtoList;
    }

    public void changeComment(CustomPrincipal customPrincipal, Long commentId, CommentUpdateRequestDto commentUpdateRequestDto) {

        UUID userUuid = customPrincipal.uuid();

        // Comment 조회
        Comment comment = commentUtil.getCommentByCommentId(userUuid, commentId);

        // 작성자 조회 및 검증
        commentUtil.validateCommentAuthor(comment, userUuid);

        comment.changeContent(commentUpdateRequestDto.content());
    }

    @Transactional
    public void removeComment(CustomPrincipal customPrincipal, Long commentId) {

        UUID userUuid = customPrincipal.uuid();

        // Comment 조회
        Comment comment = commentUtil.getCommentByCommentId(userUuid, commentId);

        // 작성자 조회 및 검증
        commentUtil.validateCommentAuthor(comment, userUuid);

        commentRepository.delete(comment);
    }

    @Transactional
    private Slice<Comment> findComment(UUID userUuid, Long postId, Long cursor) {

        Pageable pageable = PageRequest.of(0, SIZE);
        Slice<Comment> commentSlice;

        // postId == null
        if(postId == null) {
            if(cursor == null)
                commentSlice = commentRepository.findFirstSlice(userUuid, pageable);
            else
                commentSlice = commentRepository.findNextSlice(userUuid, pageable, cursor);

            return commentSlice;
        }

        // 첫 조회: 커서x, 커서 있으면 커서 기준 조회
        if(cursor == null)
            commentSlice = commentRepository.findFirstSlice(postId, pageable);
        else
            commentSlice = commentRepository.findNextSlice(postId, pageable, cursor);

        return commentSlice;
    }
}
