package com.languageworld.be.domain.post.controller;

import com.languageworld.be.domain.post.dto.request.CreateCommentRequestDto;
import com.languageworld.be.domain.post.dto.response.PostCommentResponseDto;
import com.languageworld.be.domain.post.dto.response.UserCommentResponseDto;
import com.languageworld.be.domain.post.service.CommentService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    // TODO 댓글 작성
    @PostMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<Void>> createComment(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                           @PathVariable Long postId,
                                                           @RequestBody CreateCommentRequestDto createCommentRequestDto) {

        commentService.createComment(customPrincipal, postId, createCommentRequestDto);

        return ResponseEntityUtil.success(SuccessCode.COMMENT_CREATE_SUCCESS);
    }

    // TODO 인증된 사용자가 작성한 댓글 전체 조회(postId + 게시글 제목 + 댓글 요약 + 댓글 id)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<UserCommentResponseDto>>> findAuthenticatedUserCommentList(@AuthenticationPrincipal CustomPrincipal customPrincipal) {

        List<UserCommentResponseDto> userCommentResponseDtoList = commentService.findAuthenticatedUserCommentList(customPrincipal);

        return ResponseEntityUtil.success(SuccessCode.AUTHENTICATED_USER_ALL_COMMENT_FIND_SUCCESS, userCommentResponseDtoList);
    }

    // TODO 특정 사용자가 작성한 댓글 전체 조회
    @GetMapping("/{userUuid}")
    public ResponseEntity<ApiResponse<List<UserCommentResponseDto>>> findSpecificUserCommentList(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                                                     @PathVariable UUID userUuid) {

        List<UserCommentResponseDto> userCommentResponseDtoList = commentService.findSpecificUserCommentList(customPrincipal, userUuid);

        return ResponseEntityUtil.success(SuccessCode.SPECIFIC_USER_ALL_COMMENT_FIND_SUCCESS, userCommentResponseDtoList);
    }

    // TODO 해당 게시글 댓글 전체 조회(순수 댓글 + 댓글 단 사람 정보: 프로필 + 이름 + 댓글 id)
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<List<PostCommentResponseDto>>> findPostCommentList(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                                             @PathVariable Long postId) {

        List<PostCommentResponseDto> postCommentResponseDtoList = commentService.findPostCommentList(customPrincipal, postId);

        return ResponseEntityUtil.success(SuccessCode.POST_ALL_COMMENT_FIND_SUCCESS, postCommentResponseDtoList);
    }

    // TODO 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> removeComment(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                           @PathVariable Long commentId) {

        commentService.removeComment(customPrincipal, commentId);

        return ResponseEntityUtil.success(SuccessCode.COMMENT_REMOVE_SUCCESS);
    }
}
