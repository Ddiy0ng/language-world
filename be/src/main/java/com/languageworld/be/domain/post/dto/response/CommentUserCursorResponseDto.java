package com.languageworld.be.domain.post.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CommentUserCursorResponseDto(

        Long nextCursor,
        boolean hasNext,
        List<CommentUserResponseDto> commentUserResponseDtoList
){

    public static CommentUserCursorResponseDto from(Long nextCursor, boolean hasNext, List<CommentUserResponseDto> commentUserResponseDtoList) {

        return CommentUserCursorResponseDto.builder()
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .commentUserResponseDtoList(commentUserResponseDtoList)
                .build();
    }
}
