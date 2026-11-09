package com.languageworld.be.domain.post.dto.response;

import com.languageworld.be.domain.post.entity.Comment;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentPostResponseDto(

        UserSummaryResponseDto userSummaryResponseDto,
        Long commentId,
        String content,
        LocalDateTime modifiedAt
) {

    public static CommentPostResponseDto from(Comment comment) {

        return CommentPostResponseDto.builder()
                .userSummaryResponseDto(UserSummaryResponseDto.from(comment.getAuthor()))
                .commentId(comment.getId())
                .content(comment.getContentText())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }
}
