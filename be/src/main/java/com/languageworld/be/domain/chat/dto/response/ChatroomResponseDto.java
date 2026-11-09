package com.languageworld.be.domain.chat.dto.response;

import com.languageworld.be.domain.chat.entity.Chatroom;
import com.languageworld.be.domain.chat.enumGroup.ChatroomTypeCode;
import lombok.Builder;

@Builder
public record ChatroomResponseDto(

        Long chatroomId,

        ChatroomTypeCode chatroomType,

        int chatroomSize,

        String chatroomTitle
) {

    public static ChatroomResponseDto from(Chatroom chatroom) {

        return ChatroomResponseDto.builder()
                .chatroomId(chatroom.getId())
                .chatroomType(chatroom.getChatroomType())
                .chatroomSize(chatroom.getChatroomSize())
                .chatroomTitle(chatroom.getChatroomTitle())
                .build();
    }
}
