package com.languageworld.be.domain.post.dto.response;

import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponseDto(

        PostTypeCode postTypeCode,
        Long postId,
        String title,
        String postImageUrl,
        LocalDateTime modifiedAt
) {

    public static PostResponseDto from(Post post) {

        String imageUrl = post.getPostImageList().isEmpty() ? null : post.getPostImageList().get(0).getPostImageUrl();

        PostResponseDto postResponseDto = PostResponseDto.builder()
                .postTypeCode(post.getPostType())
                .postId(post.getId())
                .title(post.getTitle())
                .postImageUrl(imageUrl)
                .modifiedAt(post.getModifiedAt())
                .build();

        return postResponseDto;
    }
}
