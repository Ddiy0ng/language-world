package com.languageworld.be.domain.post.dto.response;

import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import lombok.Builder;

@Builder
public record PostSummaryResponseDto (

        UserSummaryResponseDto userSummaryResponseDto,
        PostResponseDto postResponseDto,
        long commentSize,
        long likeSize
) {

    public static PostSummaryResponseDto from(Post post, long commentSize, long likeSize) {

        PostSummaryResponseDto postSummaryResponseDto = PostSummaryResponseDto.builder()
                .userSummaryResponseDto(UserSummaryResponseDto.from(post.getAuthor()))
                .postResponseDto(PostResponseDto.from(post))
                .commentSize(commentSize)
                .likeSize(likeSize)
                .build();

        return postSummaryResponseDto;
    }
}
