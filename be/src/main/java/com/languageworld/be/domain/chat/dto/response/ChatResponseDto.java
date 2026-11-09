package com.languageworld.be.domain.chat.dto.response;

import com.languageworld.be.domain.chat.entity.Chat;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatResponseDto(

        Long chatId,

        String chatMessage,

        UserSummaryResponseDto senderInfo,

        Long chatroomId,

        LocalDateTime createdAt

) {

    public static ChatResponseDto from(Chat chat,UserSummaryResponseDto userSummaryResponseDto, Long chatroomId) {

        return ChatResponseDto.builder()
                .chatId(chat.getId())
                .chatMessage(chat.getChatMessage())
                .senderInfo(userSummaryResponseDto)
                .chatroomId(chatroomId)
                .createdAt(chat.getCreatedAt())
                .build();

    }
}
