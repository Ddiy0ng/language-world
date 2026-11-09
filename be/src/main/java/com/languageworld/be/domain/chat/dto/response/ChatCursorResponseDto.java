package com.languageworld.be.domain.chat.dto.response;

import com.languageworld.be.domain.post.dto.response.CommentPostCursorResponseDto;
import com.languageworld.be.domain.post.dto.response.CommentPostResponseDto;
import lombok.Builder;

import java.util.List;

@Builder
public record ChatCursorResponseDto (

        Long nextCursor,
        boolean hasNext,
        List<ChatResponseDto> chatResponseDtoList
){

    public static ChatCursorResponseDto from(Long nextCursor, boolean hasNext, List<ChatResponseDto> chatResponseDtoList) {

        return ChatCursorResponseDto.builder()
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .chatResponseDtoList(chatResponseDtoList)
                .build();
    }
}
