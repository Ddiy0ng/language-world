package com.languageworld.be.domain.post.dto.response;

import com.languageworld.be.domain.post.entity.Like;
import lombok.Builder;

@Builder
public record LikePostResponseDto(

        PostResponseDto postResponseDto,
        IsLikedResponseDto isLikedResponseDto
) {

    public static LikePostResponseDto from(Like like) {

        LikePostResponseDto likePostResponseDto = LikePostResponseDto.builder()
                .postResponseDto(PostResponseDto.from(like.getPost()))
                .isLikedResponseDto(IsLikedResponseDto.from(like.getId(), true))
                .build();

        return likePostResponseDto;
    }
}
