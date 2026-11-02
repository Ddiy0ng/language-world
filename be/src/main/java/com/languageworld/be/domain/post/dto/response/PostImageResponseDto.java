package com.languageworld.be.domain.post.dto.response;

import com.languageworld.be.domain.post.entity.PostImage;
import lombok.Builder;

@Builder
public record PostImageResponseDto(

        Long id,
        String postImageUrl,
        int imageOrder
) {

    public static PostImageResponseDto from(PostImage postImage) {

        PostImageResponseDto postImageResponseDto = PostImageResponseDto.builder()
                .id(postImage.getId())
                .postImageUrl(postImage.getPostImageUrl())
                .imageOrder(postImage.getImageOrder())
                .build();

        return postImageResponseDto;
    }
}
