package com.languageworld.be.domain.chat.dto.response;

import com.languageworld.be.domain.chat.entity.Chat;
import com.languageworld.be.domain.chat.entity.Chatroom;
import com.languageworld.be.domain.chat.enumGroup.ChatroomTypeCode;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import com.languageworld.be.domain.user.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatroomResponseDto(

        Long chatroomId,
        ChatroomTypeCode chatroomType,
        int chatroomSize,
        String chatroomTitle,
        UserSummaryResponseDto opposite,      // 1:1 방의 상대, 그룹방은 null
        String lastChatMessage,
        LocalDateTime lastChatCreatedAt
) {

    public static ChatroomResponseDto from(Chatroom chatroom, User opposite, Chat lastChat) {

        return ChatroomResponseDto.builder()
                .chatroomId(chatroom.getId())
                .chatroomType(chatroom.getChatroomType())
                .chatroomSize(chatroom.getChatroomSize())
                .chatroomTitle(chatroom.getChatroomTitle())
                .opposite(opposite == null ? null : UserSummaryResponseDto.from(opposite))
                .lastChatMessage(lastChat == null ? null : lastChat.getChatMessage())
                .lastChatCreatedAt(lastChat == null ? null : lastChat.getCreatedAt())
                .build();
    }
}
