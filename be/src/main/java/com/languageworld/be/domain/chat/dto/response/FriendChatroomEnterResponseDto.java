package com.languageworld.be.domain.chat.dto.response;

import lombok.Builder;

@Builder
public record FriendChatroomEnterResponseDto(

        Long chatroomId
) {

    public static FriendChatroomEnterResponseDto from(Long chatroomId) {

        return FriendChatroomEnterResponseDto.builder()
                .chatroomId(chatroomId)
                .build();
    }
}
