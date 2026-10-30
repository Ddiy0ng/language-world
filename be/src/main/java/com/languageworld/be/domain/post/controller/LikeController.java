package com.languageworld.be.domain.post.controller;

import com.languageworld.be.domain.post.dto.response.LikePostResponseDto;
import com.languageworld.be.domain.post.dto.response.PostSummaryResponseDto;
import com.languageworld.be.domain.post.service.LikeService;
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
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    // TODO 좋아요 추가
    @RequestMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<Void>> createLike(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                        @PathVariable Long postId) {

        likeService.createLike(customPrincipal, postId);

        return ResponseEntityUtil.success(SuccessCode.LIKE_CREATE_SUCCESS);
    }

    // TODO 인증된 사용자가 좋아요 추가한 게시글 전체 조회(postId + 게시글 제목)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<LikePostResponseDto>>> findAuthenticatedUserLikePostList(@AuthenticationPrincipal CustomPrincipal customPrincipal) {

        List<LikePostResponseDto> likePostResponseDtoList = likeService.findAuthenticatedUserLikePostList(customPrincipal);

        return ResponseEntityUtil.success(SuccessCode.AUTHENTICATED_USER_ALL_LIKE_FIND_SUCCESS, likePostResponseDtoList);
    }

    // TODO 특정 사용자가 좋아요 추가한 게시글 전체 조회
    @GetMapping("/{userUuid}")
    public ResponseEntity<ApiResponse<List<LikePostResponseDto>>> findSpecificUserLikePostList(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                                                  @PathVariable UUID userUuid) {

        List<LikePostResponseDto> likePostResponseDtoList = likeService.findSpecificUserLikePostList(customPrincipal, userUuid);

        return ResponseEntityUtil.success(SuccessCode.SPECIFIC_USER_ALL_LIKE_FIND_SUCCESS, likePostResponseDtoList);
    }

    // TODO 좋아요 삭제
    @DeleteMapping("/likeId")
    public ResponseEntity<ApiResponse<Void>> removeLike(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                        @PathVariable Long likeId) {

        likeService.removeLike(customPrincipal, likeId);

        return ResponseEntityUtil.success(SuccessCode.LIKE_REMOVE_SUCCESS);
    }
}
