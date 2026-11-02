package com.languageworld.be.domain.post.dto.response;

import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import com.languageworld.be.domain.user.enumGroup.NationCode;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record PostResponseDto (

        UserSummaryResponseDto userSummaryResponseDto,
        PostTypeCode postTypeCode,
        Long postId,
        String title,
        String content,
        List<PostImageResponseDto> postImageUrlList,
        LocalDateTime modifiedAt,
        long commentSize,
        long likeSize
){

    public static PostResponseDto from(Post post, long commentSize, long likeSize) {

        PostResponseDto postResponseDto = PostResponseDto.builder()
                .userSummaryResponseDto(UserSummaryResponseDto.from(post.getAuthor()))
                .postTypeCode(post.getPostType())
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContentText())
                .postImageUrlList(post.getPostImageList().isEmpty()?List.of():post.getPostImageList().stream().map(PostImageResponseDto::from).toList())
                .modifiedAt(post.getModifiedAt())
                .commentSize(commentSize)
                .likeSize(likeSize)
                .build();

        return postResponseDto;
    }
}
