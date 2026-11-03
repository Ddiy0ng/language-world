package com.languageworld.be.domain.post.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record IsLikedResponseDto(

        Long likeId,

        @NotNull
        boolean isLiked
) {

    public static IsLikedResponseDto from(Long likeId, boolean isLiked) {

        IsLikedResponseDto isLikedResponseDto = IsLikedResponseDto.builder()
                .likeId(!isLiked?null:likeId)
                .isLiked(isLiked)
                .build();

        return isLikedResponseDto;
    }
}
