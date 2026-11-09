package com.languageworld.be.domain.chat.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ChatroomCursorResponseDto(

        Long nextCursor,
        boolean hasNext,
        List<ChatroomResponseDto> chatroomResponseDtoList
){

    public static ChatroomCursorResponseDto from(Long nextCursor, boolean hasNext, List<ChatroomResponseDto> chatroomResponseDtoList) {

        return ChatroomCursorResponseDto.builder()
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .chatroomResponseDtoList(chatroomResponseDtoList)
                .build();
    }
}
