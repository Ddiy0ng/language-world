package com.languageworld.be.domain.chat.dto.response;

import com.languageworld.be.domain.chat.entity.Chatroom;
import com.languageworld.be.domain.user.entity.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record WebSocketChatroomMemberResponseDto(

        Long chatroomId,
        UUID targetUuid,     // 들어오거나 나간 사람
        UUID hostUuid
) {
    public static WebSocketChatroomMemberResponseDto of(Chatroom chatroom, User target) {

        return WebSocketChatroomMemberResponseDto.builder()
                .chatroomId(chatroom.getId())
                .targetUuid(target.getUuid())
                .hostUuid(chatroom.getHost().getUuid())
                .build();
    }
}
