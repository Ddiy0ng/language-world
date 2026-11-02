package com.languageworld.be.domain.post.dto.response;

import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.post.entity.PostImage;
import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PostSummaryResponseDto (

//        전체, 일상, 헬프 + 본문 또는 제목 검색, 최신순 정렬, 페이지네이션 + 인덱싱): 사진1, 제목, 댓글 수, 좋아요 수
        UUID authorUuid,
        String authorProfileImageUrl,
        String authorName,
        PostTypeCode postTypeCode,
        Long postId,
        String title,
        String postImageUrl,
        LocalDateTime modifiedAt,
        long commentSize,
        long likeSize
) {

    public static PostSummaryResponseDto from(Post post, long commentSize, long likeSize) {

        String imageUrl = post.getPostImageList().isEmpty() ? null : post.getPostImageList().get(0).getPostImageUrl();

        PostSummaryResponseDto postSummaryResponseDto = PostSummaryResponseDto.builder()
                .authorUuid(post.getAuthor().getUuid())
                .authorProfileImageUrl(post.getAuthor().getProfileImageUrl())
                .authorName(post.getAuthor().getName())
                .postTypeCode(post.getPostType())
                .postId(post.getId())
                .title(post.getTitle())
                .postImageUrl(imageUrl)
                .modifiedAt(post.getModifiedAt())
                .commentSize(commentSize)
                .likeSize(likeSize)
                .build();

        return postSummaryResponseDto;
    }
}
