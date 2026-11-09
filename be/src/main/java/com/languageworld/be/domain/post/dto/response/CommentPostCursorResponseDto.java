package com.languageworld.be.domain.post.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CommentPostCursorResponseDto(

        Long nextCursor,
        boolean hasNext,
        List<CommentPostResponseDto> commentPostResponseDtoList
){

    public static CommentPostCursorResponseDto from(Long nextCursor, boolean hasNext, List<CommentPostResponseDto> commentPostResponseDtoList) {

        return CommentPostCursorResponseDto.builder()
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .commentPostResponseDtoList(commentPostResponseDtoList)
                .build();
    }
}
