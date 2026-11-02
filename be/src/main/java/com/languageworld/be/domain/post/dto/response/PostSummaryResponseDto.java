package com.languageworld.be.domain.post.dto.response;

import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record PostSummaryResponseDto (

        UserSummaryResponseDto userSummaryResponseDto,
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
                .userSummaryResponseDto(UserSummaryResponseDto.from(post.getAuthor()))
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
