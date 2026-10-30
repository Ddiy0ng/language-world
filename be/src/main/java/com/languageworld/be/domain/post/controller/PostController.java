package com.languageworld.be.domain.post.controller;

import com.languageworld.be.domain.post.dto.request.PostCreateRequestDto;
import com.languageworld.be.domain.post.dto.request.PostUpdateRequestDto;
import com.languageworld.be.domain.post.dto.response.PostResponseDto;
import com.languageworld.be.domain.post.dto.response.PostSummaryResponseDto;
import com.languageworld.be.domain.post.service.PostService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    // TODO 게시글 등록: 제목, 게시글 타입, 본문(이미지 허용-최대 3장, 10MB 제한)
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPost(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                        @Valid @RequestPart(value = "requestBody") PostCreateRequestDto postCreateRequestDto,
                                                        @RequestPart(value = "images", required = false) List<MultipartFile> multipartFileList
                                                        ) {

        postService.createPost(customPrincipal, postCreateRequestDto, multipartFileList);

        return ResponseEntityUtil.success(SuccessCode.POST_CREATE_SUCCESS);
    }

    // TODO 내 게시글 전체 조회
    @GetMapping("me")
    public ResponseEntity<List<PostSummaryResponseDto>> findAuthenticatedUserPostList(@AuthenticationPrincipal CustomPrincipal customPrincipal) {

        List<PostSummaryResponseDto> postSummaryResponseDtoList = postService.findAuthenticatedUserAllPost(customPrincipal);

        return ResponseEntityUtil.success(SuccessCode.AUTHENTICATED_USER_ALL_POST_FIND_SUCESS, postSummaryResponseDtoList);
    }

    // TODO 특정 사용자의 게시글 조회
    @GetMapping("/{userUuid}")
    public ResponseEntity<ApiResponse<List<PostSummaryResponseDto>>> findSpecificUserPostList(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                                             @PathVariable UUID userUuid) {

        List<PostSummaryResponseDto> postSummaryResponseDtoList = postService.findSpecificUserAllPost(customPrincipal, userUuid);

        return ResponseEntityUtil.success(SuccessCode.SPECIFIC_USER_ALL_POST_FIND_SUCESS, postSummaryResponseDtoList);
    }

    // TODO 게시글 요약 검색 V1(게시글 타입에 따른 필터링-전체, 일상, 헬프 + 본문 또는 제목 검색, 최신순 정렬, 페이지네이션 + 인덱싱): 사진1, 제목, 댓글 수, 좋아요 수
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostSummaryResponseDto>>> searchPost(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                                @RequestParam(required = false) String keyword) {

        List<PostSummaryResponseDto> postSummaryResponseDtoList = postService.searchPost(customPrincipal, keyword);

        return ResponseEntityUtil.success(SuccessCode.POST_SEARCH_SUCCESS, postSummaryResponseDtoList);
    }

    // TODO 게시글 상세 조회: 게시글 +  인증 유저의 좋아요 여부 확인
    @GetMapping("/detail/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> findPostDetail(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                       @PathVariable Long postId) {

        PostResponseDto postResponseDto = postService.findPostDetail(customPrincipal, postId);

        return ResponseEntityUtil.success(SuccessCode.POST_DETAIL_FIND_SUCCESS, postResponseDto);
    }

    // TODO 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> changePost(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                        @PathVariable Long postId,
                                                        @Valid @RequestPart("requestBody") PostUpdateRequestDto postUpdateRequestDto,
                                                        @RequestPart(value = "images", required = false) List<MultipartFile> multipartFileList) {

        postService.changePost(customPrincipal, postId, postUpdateRequestDto, multipartFileList);

        return ResponseEntityUtil.success(SuccessCode.POST_CHANGE_SUCCESS);
    }

    // TODO 게시글 삭제
    @DeleteMapping("/postId")
    public ResponseEntity<ApiResponse<Void>> removePost(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                        @PathVariable Long postId) {

        postService.removePost(customPrincipal, postId);

        return ResponseEntityUtil.success(SuccessCode.POST_REMOVE_SUCCESS);
    }
}
